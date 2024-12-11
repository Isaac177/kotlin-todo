package com.example.todo.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todo.data.local.PreferencesManager
import com.example.todo.data.local.TodoDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class TodoBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: TodoDatabase,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val backupDir = File(applicationContext.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            // Generate backup filename with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
            val backupFile = File(backupDir, "todo_backup_$timestamp.db")

            // Copy database file to backup location
            database.close()
            val dbFile = applicationContext.getDatabasePath("todo_database")
            dbFile.inputStream().use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Update last backup time
            preferencesManager.updateLastBackupTime()

            // Keep only last 5 backups
            cleanupOldBackups(backupDir)

            Result.success()
        } catch (e: Exception) {
            Log.e("TodoBackupWorker", "Backup failed", e)
            Result.failure()
        }
    }

    private fun cleanupOldBackups(backupDir: File) {
        val backups = backupDir.listFiles { file ->
            file.name.startsWith("todo_backup_") && file.name.endsWith(".db")
        }

        backups?.sortByDescending { it.lastModified() }
        backups?.drop(5)?.forEach { it.delete() }
    }
}

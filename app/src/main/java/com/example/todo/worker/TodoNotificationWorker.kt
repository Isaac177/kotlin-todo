package com.example.todo.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todo.data.repository.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class TodoNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val todoRepository: TodoRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "todo_notifications"
        const val NOTIFICATION_ID = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get upcoming todos
            val upcomingTodos = todoRepository.getUpcomingTodos()
            
            if (upcomingTodos.isNotEmpty()) {
                createNotificationChannel()
                
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Upcoming Todo Tasks")
                    .setContentText("You have ${upcomingTodos.size} upcoming tasks")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Todo Notifications"
            val descriptionText = "Notifications for upcoming todo tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

package com.example.todo

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.example.todo.worker.TodoNotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TodoApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<TodoNotificationWorker>(
            1, TimeUnit.HOURS,  // Check every hour
            15, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "todo_notification_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            repeatingRequest
        )
    }
}

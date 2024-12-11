package com.example.todo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.todo.data.local.PreferencesManager
import com.example.todo.worker.TodoNotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val workManager: WorkManager
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                
                val notifications = preferencesManager.getNotificationsEnabled()
                val darkMode = preferencesManager.getDarkModeEnabled()
                val notificationTime = preferencesManager.getNotificationTime()
                val autoBackup = preferencesManager.getAutoBackupEnabled()
                val backupFrequency = preferencesManager.getBackupFrequency()
                val lastBackup = preferencesManager.getLastBackupTime()
                val lastSync = preferencesManager.getLastSync()
                
                _state.value = SettingsState(
                    notificationsEnabled = notifications,
                    darkModeEnabled = darkMode,
                    notificationTime = notificationTime,
                    autoBackupEnabled = autoBackup,
                    backupFrequency = backupFrequency,
                    lastBackupTime = lastBackup,
                    lastSyncTime = lastSync
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load settings: ${e.message}"
                )
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            _state.value = _state.value.copy(notificationsEnabled = enabled)
            
            if (enabled) {
                scheduleNotificationWork()
            } else {
                workManager.cancelUniqueWork("todo_notification_work")
            }
        }
    }

    private fun scheduleNotificationWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<TodoNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "todo_notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWork
        )
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkModeEnabled(enabled)
            _state.value = _state.value.copy(darkModeEnabled = enabled)
        }
    }

    fun updateNotificationTime(hours: Int) {
        viewModelScope.launch {
            preferencesManager.setNotificationTime(hours)
            _state.value = _state.value.copy(notificationTime = hours)
            if (_state.value.notificationsEnabled) {
                scheduleNotificationWork()
            }
        }
    }

    fun toggleAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoBackupEnabled(enabled)
            _state.value = _state.value.copy(autoBackupEnabled = enabled)
            if (enabled) {
                scheduleBackupWork()
            } else {
                workManager.cancelUniqueWork("todo_backup_work")
            }
        }
    }

    fun updateBackupFrequency(days: Int) {
        viewModelScope.launch {
            preferencesManager.setBackupFrequency(days)
            _state.value = _state.value.copy(backupFrequency = days)
            if (_state.value.autoBackupEnabled) {
                scheduleBackupWork()
            }
        }
    }

    private fun scheduleBackupWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()

        val backupWork = PeriodicWorkRequestBuilder<TodoNotificationWorker>(
            _state.value.backupFrequency.toLong(), TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "todo_backup_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            backupWork
        )
    }
}

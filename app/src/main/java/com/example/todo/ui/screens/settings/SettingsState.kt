package com.example.todo.ui.screens.settings

data class SettingsState(
    val notificationsEnabled: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val notificationTime: Int = 24, // Hours before due date
    val autoBackupEnabled: Boolean = false,
    val backupFrequency: Int = 7, // Days between backups
    val lastBackupTime: Long = 0,
    val lastSyncTime: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

package com.example.todo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val NOTIFICATION_TIME = intPreferencesKey("notification_time")
        val LAST_SYNC = longPreferencesKey("last_sync")
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val BACKUP_FREQUENCY = intPreferencesKey("backup_frequency")
        val LAST_BACKUP = longPreferencesKey("last_backup")
        val USER_ID = intPreferencesKey("user_id")
        val THEME_MODE = intPreferencesKey("theme_mode")
    }

    val settingsFlow: Flow<SettingsData> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SettingsData(
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: false,
                darkModeEnabled = preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false,
                notificationTime = preferences[PreferencesKeys.NOTIFICATION_TIME] ?: 24,
                autoBackupEnabled = preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] ?: false,
                backupFrequency = preferences[PreferencesKeys.BACKUP_FREQUENCY] ?: 7,
                lastBackupTime = preferences[PreferencesKeys.LAST_BACKUP] ?: 0,
                lastSyncTime = preferences[PreferencesKeys.LAST_SYNC] ?: 0
            )
        }

    val userId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    val themeMode: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: 0
        }

    suspend fun getNotificationsEnabled(): Boolean {
        return context.dataStore.data.first()[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: false
    }

    suspend fun getDarkModeEnabled(): Boolean {
        return context.dataStore.data.first()[PreferencesKeys.DARK_MODE_ENABLED] ?: false
    }

    suspend fun getNotificationTime(): Int {
        return context.dataStore.data.first()[PreferencesKeys.NOTIFICATION_TIME] ?: 24
    }

    suspend fun getAutoBackupEnabled(): Boolean {
        return context.dataStore.data.first()[PreferencesKeys.AUTO_BACKUP_ENABLED] ?: false
    }

    suspend fun getBackupFrequency(): Int {
        return context.dataStore.data.first()[PreferencesKeys.BACKUP_FREQUENCY] ?: 7
    }

    suspend fun getLastBackupTime(): Long {
        return context.dataStore.data.first()[PreferencesKeys.LAST_BACKUP] ?: 0
    }

    suspend fun getLastSync(): Long {
        return context.dataStore.data.first()[PreferencesKeys.LAST_SYNC] ?: 0L
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setNotificationTime(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME] = hours
        }
    }

    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_BACKUP_ENABLED] = enabled
        }
    }

    suspend fun setBackupFrequency(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKUP_FREQUENCY] = days
        }
    }

    suspend fun updateLastBackupTime() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_BACKUP] = System.currentTimeMillis()
        }
    }

    suspend fun updateLastSync() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC] = System.currentTimeMillis()
        }
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun setUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }

    data class SettingsData(
        val notificationsEnabled: Boolean,
        val darkModeEnabled: Boolean,
        val notificationTime: Int,
        val autoBackupEnabled: Boolean,
        val backupFrequency: Int,
        val lastBackupTime: Long,
        val lastSyncTime: Long
    )
}

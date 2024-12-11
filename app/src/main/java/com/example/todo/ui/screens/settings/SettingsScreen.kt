package com.example.todo.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.ui.components.LoadingOverlay
import com.example.todo.ui.utils.NotificationPermissionHelper
import com.example.todo.ui.utils.RequestNotificationPermission
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    RequestNotificationPermission(
        onPermissionGranted = {
            if (state.notificationsEnabled) {
                viewModel.toggleNotifications(true)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Divider()

        // Notifications Card
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Enable Notifications")
                        if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
                            Text(
                                "Permission required",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled || NotificationPermissionHelper.hasNotificationPermission(context)) {
                                viewModel.toggleNotifications(enabled)
                            }
                        }
                    )
                }

                if (state.notificationsEnabled) {
                    Divider()
                    Text("Notification Time (hours before due)")
                    Slider(
                        value = state.notificationTime.toFloat(),
                        onValueChange = { viewModel.updateNotificationTime(it.toInt()) },
                        valueRange = 1f..48f,
                        steps = 47
                    )
                    Text("${state.notificationTime} hours")
                }
            }
        }

        // Theme Card
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode")
                    Switch(
                        checked = state.darkModeEnabled,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }
        }

        // Backup Card
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Data Backup",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto Backup")
                    Switch(
                        checked = state.autoBackupEnabled,
                        onCheckedChange = { viewModel.toggleAutoBackup(it) }
                    )
                }

                if (state.autoBackupEnabled) {
                    Divider()
                    Text("Backup Frequency (days)")
                    Slider(
                        value = state.backupFrequency.toFloat(),
                        onValueChange = { viewModel.updateBackupFrequency(it.toInt()) },
                        valueRange = 1f..30f,
                        steps = 29
                    )
                    Text("Every ${state.backupFrequency} days")
                }

                if (state.lastBackupTime > 0) {
                    Text(
                        "Last backup: ${dateFormat.format(Date(state.lastBackupTime))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    if (state.isLoading) {
        LoadingOverlay()
    }
}

package com.example.todo.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

object NotificationPermissionHelper {
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            }
        }

        if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
            SideEffect {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

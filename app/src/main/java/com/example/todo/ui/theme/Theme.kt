package com.example.todo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryGreenLight,
    tertiary = AnalogBlueGreen,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onTertiary = OnPrimary,
    onBackground = OnBackground,
    onSurface = OnSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenLight,
    secondary = PrimaryGreen,
    tertiary = AnalogBlueGreen,
    background = OnBackground,
    surface = OnSurface,
    onPrimary = Background,
    onSecondary = Background,
    onTertiary = Background,
    onBackground = Background,
    onSurface = Background,
)

@Composable
fun TodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

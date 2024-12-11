package com.example.todo.ui.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

object AnimationUtils {
    // Standard durations
    const val SHORT_ANIMATION_DURATION = 150
    private const val MEDIUM_ANIMATION_DURATION = 300
    const val LONG_ANIMATION_DURATION = 450

    @Composable
    fun fadeInOut(
        visible: Boolean,
        duration: Int = MEDIUM_ANIMATION_DURATION,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(duration)),
            exit = fadeOut(animationSpec = tween(duration)),
            content = content
        )
    }

    @Composable
    fun slideInOut(
        visible: Boolean,
        duration: Int = MEDIUM_ANIMATION_DURATION,
        content: @Composable AnimatedVisibilityScope.() -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(duration)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(duration)
            ),
            content = content
        )
    }

    @Composable
    fun Modifier.pulse(
        duration: Int = MEDIUM_ANIMATION_DURATION,
        minScale: Float = 0.95f
    ): Modifier {
        return this.then(
            Modifier.scale(
                animateFloatAsState(
                    targetValue = minScale,
                    animationSpec = infiniteRepeatable(
                        animation = tween(duration),
                        repeatMode = RepeatMode.Reverse
                    ), label = ""
                ).value
            )
        )
    }
}

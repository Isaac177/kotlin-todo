package com.example.todo.ui.screens.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.example.todo.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        if (viewModel.isUserLoggedIn()) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = { if (isPlaying) 1f else 0f },
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Todo App",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

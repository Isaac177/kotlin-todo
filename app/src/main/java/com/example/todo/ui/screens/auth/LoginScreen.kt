package com.example.todo.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    viewModel.validateEmail(it)
                },
                label = { Text("Email") },
                singleLine = true,
                isError = authState.emailError != null,
                supportingText = {
                    authState.emailError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    viewModel.validatePassword(it)
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = authState.passwordError != null,
                supportingText = {
                    authState.passwordError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = authState.error != null) {
                Text(
                    text = authState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !authState.isLoading
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.clearErrors()
                    onNavigateToRegister()
                }
            ) {
                Text("Don't have an account? Register")
            }
        }

        if (authState.isLoading) {
            LoadingOverlay()
        }
    }
}

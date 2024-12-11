package com.example.todo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todo.navigation.TodoScreen
import com.example.todo.ui.components.BottomBar
import com.example.todo.ui.components.TodoDialog
import com.example.todo.ui.screens.auth.LoginScreen
import com.example.todo.ui.screens.auth.RegisterScreen
import com.example.todo.ui.screens.home.HomeScreen
import com.example.todo.ui.screens.profile.ProfileScreen
import com.example.todo.ui.screens.settings.SettingsScreen
import com.example.todo.ui.screens.splash.SplashScreen
import com.example.todo.ui.theme.TodoTheme
import com.example.todo.ui.viewmodels.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    var showAddDialog by remember { mutableStateOf(false) }
    val themeMode by viewModel.themeMode.collectAsState()

    TodoTheme(
        darkTheme = when (themeMode) {
            1 -> false
            2 -> true
            else -> isSystemInDarkTheme()
        }
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val showBottomBar = currentRoute in listOf(
            TodoScreen.Home.route,
            TodoScreen.Profile.route,
            TodoScreen.Settings.route
        )

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        navController = navController,
                        onCreateClick = { showAddDialog = true }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    startDestination = TodoScreen.Splash.route
                ) {
                    composable(TodoScreen.Splash.route) {
                        SplashScreen(
                            onNavigateToAuth = {
                                navController.navigate(TodoScreen.Login.route) {
                                    popUpTo(TodoScreen.Splash.route) { inclusive = true }
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate(TodoScreen.Home.route) {
                                    popUpTo(TodoScreen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(TodoScreen.Login.route) {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate(TodoScreen.Register.route)
                            },
                            onLoginSuccess = {
                                navController.navigate(TodoScreen.Home.route) {
                                    popUpTo(TodoScreen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(TodoScreen.Register.route) {
                        RegisterScreen(
                            onNavigateToLogin = { navController.popBackStack() },
                            onRegisterSuccess = {
                                navController.navigate(TodoScreen.Home.route) {
                                    popUpTo(TodoScreen.Register.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(TodoScreen.Home.route) {
                        HomeScreen(
                            onNavigateToAuth = {
                                navController.navigate(TodoScreen.Login.route) {
                                    popUpTo(TodoScreen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(TodoScreen.Profile.route) {
                        ProfileScreen(
                            onNavigateToAuth = {
                                navController.navigate(TodoScreen.Login.route) {
                                    popUpTo(TodoScreen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(TodoScreen.Settings.route) { SettingsScreen() }
                }

                if (showAddDialog) {
                    TodoDialog(
                        todo = null,
                        onDismiss = { showAddDialog = false },
                        onConfirm = { title, description, dueDate ->
                            viewModel.addTodo(title, description, dueDate)
                            showAddDialog = false
                        }
                    )
                }
            }
        }
    }
}

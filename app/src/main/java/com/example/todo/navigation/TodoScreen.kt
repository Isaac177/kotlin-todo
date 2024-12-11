package com.example.todo.navigation

sealed class TodoScreen(val route: String) {
    object Splash : TodoScreen("splash")
    object Login : TodoScreen("login")
    object Register : TodoScreen("register")
    object Home : TodoScreen("home")
    object Profile : TodoScreen("profile")
    object Settings : TodoScreen("settings")
}

package com.example.todo.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = TodoScreen.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Create : BottomNavItem(
        route = "create",
        title = "Create",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add
    )

    object Settings : BottomNavItem(
        route = TodoScreen.Settings.route,
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    object Profile : BottomNavItem(
        route = TodoScreen.Profile.route,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

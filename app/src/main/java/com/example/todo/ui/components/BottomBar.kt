package com.example.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todo.navigation.BottomNavItem

@Composable
fun BottomBar(
    navController: NavHostController,
    onCreateClick: () -> Unit
) {
    val screens = listOf(
        BottomNavItem.Home,
        BottomNavItem.Settings,
        BottomNavItem.Profile
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box {
        NavigationBar {
            screens.take(1).forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                screen.selectedIcon
                            } else screen.unselectedIcon,
                            contentDescription = screen.title
                        )
                    },
                    label = { Text(text = screen.title) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            // Empty space for FAB
            NavigationBarItem(
                icon = { Spacer(modifier = Modifier.width(48.dp)) },
                label = { Text("") },
                selected = false,
                onClick = { }
            )

            screens.drop(1).forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                screen.selectedIcon
                            } else screen.unselectedIcon,
                            contentDescription = screen.title
                        )
                    },
                    label = { Text(text = screen.title) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // Centered FAB
        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .offset(y = (-20).dp)
                .align(androidx.compose.ui.Alignment.TopCenter),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = BottomNavItem.Create.selectedIcon,
                contentDescription = BottomNavItem.Create.title,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

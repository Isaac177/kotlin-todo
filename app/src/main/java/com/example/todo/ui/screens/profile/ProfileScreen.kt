package com.example.todo.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        onNavigateToAuth()
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout"
                            )
                        }
                    }
                    
                    state.user?.let { user ->
                        if (state.isEditing) {
                            var name by remember { mutableStateOf(user.name) }
                            var email by remember { mutableStateOf(user.email) }
                            
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { viewModel.hideEditProfile() }) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = { viewModel.updateUser(name, email) },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text("Save")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                IconButton(onClick = { viewModel.showEditProfile() }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Statistics Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Todo Statistics",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            label = "Total",
                            value = state.todoStats.totalTodos.toString()
                        )
                        StatItem(
                            label = "Active",
                            value = state.todoStats.activeTodos.toString()
                        )
                        StatItem(
                            label = "Completed",
                            value = state.todoStats.completedTodos.toString()
                        )
                    }
                }
            }
        }

        // Loading and Error states
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        state.error?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(error)
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

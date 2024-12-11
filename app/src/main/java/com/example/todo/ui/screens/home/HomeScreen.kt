package com.example.todo.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todo.data.model.Todo
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAuth: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showSearchField by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { if (!showSearchField) Text("Todo List") },
                actions = {
                    if (showSearchField) {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            placeholder = { Text("Search todos...") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        showSearchField = false
                                        viewModel.updateSearchQuery("")
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close search"
                                    )
                                }
                            }
                        )
                    } else {
                        IconButton(onClick = { showSearchField = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }

                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            FilterType.values().forEach { filterType ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (filterType) {
                                                FilterType.ALL -> "All"
                                                FilterType.ACTIVE -> "Active"
                                                FilterType.COMPLETED -> "Completed"
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.updateFilterType(filterType)
                                        showFilterMenu = false
                                    },
                                    leadingIcon = {
                                        if (state.filterType == filterType) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortType.values().forEach { sortType ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (sortType) {
                                                SortType.DATE_ASC -> "Date ↑"
                                                SortType.DATE_DESC -> "Date ↓"
                                                SortType.TITLE_ASC -> "Title ↑"
                                                SortType.TITLE_DESC -> "Title ↓"
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.updateSortType(sortType)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (state.sortType == sortType) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.refreshTodos() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Retry")
                    }
                }
            } else if (state.todos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No todos found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.todos) { todo ->
                        TodoItem(
                            todo = todo,
                            dateFormatter = dateFormatter,
                            onClick = { viewModel.showDialog(todo) },
                            onToggleComplete = { viewModel.toggleTodoComplete(todo) },
                            onDelete = { viewModel.deleteTodo(todo) }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItem(
    todo: Todo,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (todo.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                todo.dueDate?.let { dueDate ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Due: ${dueDate.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Todo") },
            text = { Text("Are you sure you want to delete this todo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
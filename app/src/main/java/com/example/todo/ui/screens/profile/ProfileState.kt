package com.example.todo.ui.screens.profile

import com.example.todo.data.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val todoStats: TodoStats = TodoStats(),
    val themeMode: Int = 0
)

data class TodoStats(
    val totalTodos: Int = 0,
    val completedTodos: Int = 0,
    val activeTodos: Int = 0
)

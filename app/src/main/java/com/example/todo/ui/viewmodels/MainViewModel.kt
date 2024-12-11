package com.example.todo.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.local.PreferencesManager
import com.example.todo.data.model.Todo
import com.example.todo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val todoRepository: TodoRepository
) : ViewModel() {

    val themeMode: StateFlow<Int> = preferencesManager.settingsFlow
        .map { settings ->
            when {
                !settings.darkModeEnabled -> 1 // Light mode
                settings.darkModeEnabled -> 2  // Dark mode
                else -> 0 // System default
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTodo(title: String, description: String, dueDate: LocalDateTime?) {
        viewModelScope.launch {
            try {
                val userId = preferencesManager.userId.first() ?: throw IllegalStateException("User not logged in")
                todoRepository.addTodo(
                    Todo(
                        userId = userId,
                        title = title,
                        description = description,
                        dueDate = dueDate,
                        isCompleted = false
                    )
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add todo"
            }
        }
    }
}

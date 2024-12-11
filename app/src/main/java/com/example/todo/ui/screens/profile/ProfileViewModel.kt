package com.example.todo.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.local.PreferencesManager
import com.example.todo.data.repository.TodoRepository
import com.example.todo.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserData()
        loadThemeMode()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            preferencesManager.userId.collectLatest { userId ->
                userId?.let { id ->
                    try {
                        combine(
                            userRepository.getUserById(id),
                            todoRepository.getTodosByUser(id)
                        ) { user, todos ->
                            val stats = TodoStats(
                                totalTodos = todos.size,
                                completedTodos = todos.count { it.isCompleted },
                                activeTodos = todos.count { !it.isCompleted }
                            )
                            _state.update {
                                it.copy(
                                    user = user,
                                    todoStats = stats,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }.collect()
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message, isLoading = false) }
                    }
                }
            }
        }
    }

    private fun loadThemeMode() {
        viewModelScope.launch {
            preferencesManager.themeMode.collect { mode ->
                _state.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun updateUser(name: String, email: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val currentUser = _state.value.user
                currentUser?.let { user ->
                    val updatedUser = user.copy(name = name, email = email)
                    userRepository.updateUser(updatedUser)
                    _state.update {
                        it.copy(user = updatedUser, isEditing = false, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesManager.clearUserId()
        }
    }

    fun showEditProfile() {
        _state.update { it.copy(isEditing = true) }
    }

    fun hideEditProfile() {
        _state.update { it.copy(isEditing = false) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

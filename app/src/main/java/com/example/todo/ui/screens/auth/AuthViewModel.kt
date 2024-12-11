package com.example.todo.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.local.PreferencesManager
import com.example.todo.data.model.User
import com.example.todo.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            _authState.update { it.copy(emailError = "Email cannot be empty") }
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.update { it.copy(emailError = "Invalid email format") }
            false
        } else {
            _authState.update { it.copy(emailError = null) }
            true
        }
    }

    fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            _authState.update { it.copy(passwordError = "Password cannot be empty") }
            false
        } else if (password.length < 6) {
            _authState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            false
        } else {
            _authState.update { it.copy(passwordError = null) }
            true
        }
    }

    fun validateName(name: String): Boolean {
        return if (name.isEmpty()) {
            _authState.update { it.copy(nameError = "Name cannot be empty") }
            false
        } else {
            _authState.update { it.copy(nameError = null) }
            true
        }
    }

    fun login(email: String, password: String) {
        if (!validateEmail(email) || !validatePassword(password)) return

        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            try {
                userRepository.login(email, password)?.let { user ->
                    preferencesManager.setUserId(user.id)
                    _authState.update { it.copy(isSuccess = true) }
                } ?: run {
                    _authState.update { it.copy(error = "Invalid email or password") }
                }
            } catch (e: Exception) {
                _authState.update { it.copy(error = e.message ?: "An error occurred") }
            } finally {
                _authState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (!validateName(name) || !validateEmail(email) || !validatePassword(password)) return

        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = userRepository.register(
                    User(
                        name = name,
                        email = email,
                        password = password
                    )
                )
                preferencesManager.setUserId(userId.toInt())
                _authState.update { it.copy(isSuccess = true) }
            } catch (e: Exception) {
                _authState.update { it.copy(error = e.message ?: "An error occurred") }
            } finally {
                _authState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearErrors() {
        _authState.update {
            it.copy(
                error = null,
                emailError = null,
                passwordError = null,
                nameError = null
            )
        }
    }

    fun resetState() {
        _authState.value = AuthState()
    }
}

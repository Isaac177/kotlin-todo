package com.example.todo.ui.screens.auth

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null
)

package com.example.todo.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun isUserLoggedIn(): Boolean = runBlocking {
        preferencesManager.userId.first() != null
    }
}

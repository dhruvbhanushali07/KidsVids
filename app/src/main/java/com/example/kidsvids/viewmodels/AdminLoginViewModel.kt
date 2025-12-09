package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Simple UI state
data class AdminLoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

// Simple event for navigation
sealed interface AdminLoginEvent {
    data object LoginSuccess : AdminLoginEvent
}

class AdminLoginViewModel : ViewModel() { // Doesn't need any DAOs for now

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AdminLoginEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentState = _uiState.value

            // --- SIMPLE HARDCODED CHECK ---
            if (currentState.email == "admin@app.com" && currentState.password == "admin123") {
                // Success
                _eventFlow.emit(AdminLoginEvent.LoginSuccess)
            } else {
                // Failure
                _uiState.update { it.copy(isLoading = false, error = "Invalid admin credentials") }
            }
        }
    }

    fun onErrorMessageShown() {
        _uiState.update { it.copy(error = null) }
    }
}
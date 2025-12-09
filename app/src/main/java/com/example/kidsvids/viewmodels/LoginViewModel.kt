package com.example.kidsvids.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.ParentDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Data class to hold the entire UI state
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val genericError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

// 2. Sealed interface for one-time events (like navigation)
sealed interface LoginEvent {
    data object LoginSuccess : LoginEvent
}

class LoginViewModel(private val parentDao: ParentDao,
                     private val sessionViewModel: SessionViewModel) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    // This function is called when the login button is clicked
    fun onLoginClick() {
        viewModelScope.launch {
            // ... (Client-side validation remains the same)
            if (!_uiState.value.email.contains("@") || _uiState.value.password.length < 6) {
                _uiState.update { it.copy(genericError = "Please check your inputs.")}
                return@launch
            }

            // --- Start Loading ---
            _uiState.update { it.copy(isLoading = true) }

            // --- Real Database Logic ---
            try {
                val currentState = _uiState.value

                // 1. Fetch the parent from the database by email
                val parent = parentDao.getParentByEmail(currentState.email)

                // 2. Check if parent exists and if the password matches
                if (parent != null && parent.password_hash == currentState.password) {
                    // Success!
                    // 3. Update the shared session with the logged-in parent's ID
                    sessionViewModel.onParentLogin(parent.id)

                    // 4. Signal that the login was a success
                    _eventFlow.emit(LoginEvent.LoginSuccess)
                } else {
                    // Failure
                    _uiState.update { it.copy(genericError = "Invalid email or password.") }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Database error", e)
                _uiState.update { it.copy(genericError = e.message ?: "An unknown error occurred.") }

            } finally {
                // --- Stop Loading ---
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Called by the UI after the error message has been shown
    fun onErrorMessageShown() {
        _uiState.update { it.copy(genericError = null) }
    }
}
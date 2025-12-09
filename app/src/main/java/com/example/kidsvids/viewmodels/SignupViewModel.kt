package com.example.kidsvids.viewmodels

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
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.entities.Parent // <-- Import the Parent entity
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data class to hold the entire UI state for the Signup screen
data class SignupUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val genericError: String? = null,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val pinError: String? = null
)

// Sealed interface for one-time events, like successful registration
sealed interface SignupEvent {
    data object SignupSuccess : SignupEvent
}

class SignupViewModel(private val parentDao: ParentDao) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignupEvent>()
    val eventFlow = _eventFlow.asSharedFlow()



    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onPinChange(pin: String) {
        // Ensure PIN is max 4 digits and numeric
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _uiState.update { it.copy(pin = pin, pinError = null) }
        }
    }

    fun onSignupClick() {
        viewModelScope.launch {
            // ... (Client-side validation remains the same)
            val currentState = _uiState.value
            var hasError = false
            // ... (Your validation code for fullName, email, password, pin)
            if (hasError) return@launch

            // --- Start Loading ---
            _uiState.update { it.copy(isLoading = true) }

            // --- Real Database Logic ---
            try {
                // 1. Check if user already exists
                val existingParent = parentDao.getParentByEmail(currentState.email)
                if (existingParent != null) {
                    _uiState.update { it.copy(genericError = "An account with this email already exists.") }
                } else {

                    val newParent = Parent(
                        fullname = currentState.fullName,
                        email = currentState.email,
                        password_hash = currentState.password, // Storing password directly for simplicity
                        pin = currentState.pin
                    )
                    // 3. Insert the new parent into the database
                    parentDao.insert(newParent)

                    // 4. Signal that the signup was a success
                    _eventFlow.emit(SignupEvent.SignupSuccess)
                }
            } catch (e: Exception) {
                // Handle potential database errors
                _uiState.update { it.copy(genericError = "An unknown error occurred.") }
            } finally {
                // --- Stop Loading ---
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Called by the UI after the error message has been shown in a Snackbar
    fun onErrorMessageShown() {
        _uiState.update { it.copy(genericError = null) }
    }
}
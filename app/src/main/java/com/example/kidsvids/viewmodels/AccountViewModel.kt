package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.KidDao
import com.example.kidsvids.data.dao.ParentDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountUiState(
    val isLoading: Boolean = true,
    val parentEmail: String = "",
    val currentKidName: String = ""
)

class AccountViewModel(
    private val parentDao: ParentDao,
    private val kidDao: KidDao,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    // This will hold the correct PIN after it's loaded from the database
    private var correctPin: String = ""

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Get the IDs from the shared session
            val parentId = sessionViewModel.sessionState.first().parentId
            val kidId = sessionViewModel.sessionState.first().selectedKidId

            if (parentId == null) {
                _uiState.update { it.copy(isLoading = false) } // Handle error
                return@launch
            }

            // Fetch the parent to get their email and store the PIN
            val parent = parentDao.getParentById(parentId)
            if (parent != null) {
                correctPin = parent.pin
                _uiState.update { it.copy(parentEmail = parent.email) }
            }

            // Fetch the currently selected kid
            if (kidId != null) {
                kidDao.getKidById(kidId).collect { kid ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentKidName = kid?.name ?: "No profile selected"
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Simple function to check the PIN entered by the user
    fun checkPin(enteredPin: String): Boolean {
        return enteredPin == correctPin
    }
}
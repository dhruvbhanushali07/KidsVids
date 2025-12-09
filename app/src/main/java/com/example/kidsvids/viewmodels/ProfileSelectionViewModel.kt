package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.KidDao
import com.example.kidsvids.data.entities.Kid // Use your actual Kid entity from Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class to hold the entire UI state for the Profile Selection screen.
 */
data class ProfileSelectionUiState(
    val isLoading: Boolean = true,
    val profiles: List<Kid> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for the ProfileSelectionScreen.
 * It depends on the KidDao to fetch data and the SessionViewModel to know which parent is logged in.
 */
class ProfileSelectionViewModel(
    private val kidDao: KidDao,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {
    fun getSessionViewModel(): SessionViewModel {
        return sessionViewModel
    }
    private val _uiState = MutableStateFlow(ProfileSelectionUiState())
    val uiState = _uiState.asStateFlow()

    // The init block is called when the ViewModel is first created.
    init {
        loadKidProfiles()
    }

    private fun loadKidProfiles() {
        viewModelScope.launch {
            // 1. Set the initial state to loading.
            _uiState.update { it.copy(isLoading = true) }
            // 2. Get the current parentId from the SessionViewModel.
            //    We use .first() to get the most recent value from the flow.
            val parentId = sessionViewModel.sessionState.first().parentId

            if (parentId == null) {
                // 3. Handle the error case where no parent is logged in.
                _uiState.update { it.copy(isLoading = false, error = "Error: No parent is logged in.") }
                // Stop the function here if there's no parent.
                return@launch
            }

            // 4. Fetch the kids for the logged-in parent from the database.
            //    Because getKidsForParent returns a Flow, our UI will automatically
            //    update if the list of kids ever changes (e.g., after adding a new profile).
            kidDao.getKidsForParent(parentId).collect { kidsList ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        profiles = kidsList
                    )
                }
            }
        }
    }
}
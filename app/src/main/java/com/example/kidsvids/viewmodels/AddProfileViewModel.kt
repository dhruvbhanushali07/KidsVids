package com.example.kidsvids.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.KidDao
import com.example.kidsvids.data.entities.Kid
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

// UI State is updated to use an Int for the avatar resource
data class AddProfileUiState(
    val name: String = "",
    val selectedAgeCategory: String? = null,
    val selectedAvatarRes: Int? = null, // CHANGED from String? to Int?
    val isSaving: Boolean = false
)

sealed interface AddProfileEvent {
    data object SaveSuccess : AddProfileEvent
}

class AddProfileViewModel(
    private val kidDao: KidDao,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddProfileEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val ageCategoryMap = mapOf("Preschool" to 1, "Younger" to 2, "Older" to 3)

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onAgeCategorySelected(category: String) {
        _uiState.update { it.copy(selectedAgeCategory = category) }
    }

    // CHANGED to accept an Int resource ID
    fun onAvatarSelected(avatarRes: Int) {
        _uiState.update { it.copy(selectedAvatarRes = avatarRes) }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState.name.isBlank() ||
                currentState.selectedAgeCategory == null ||
                currentState.selectedAvatarRes == null
            ) {
                println("⚠️ Missing required fields: $currentState")
                return@launch
            }

            _uiState.update { it.copy(isSaving = true) }

            val parentId = sessionViewModel.sessionState.first().parentId
            if (parentId == null) {
                println("⚠️ parentId is null, cannot insert")
                _uiState.update { it.copy(isSaving = false) }
                return@launch
            }

            val ageCategoryId = ageCategoryMap[currentState.selectedAgeCategory]
            if (ageCategoryId == null) {
                println("⚠️ Invalid age category: ${currentState.selectedAgeCategory}")
                _uiState.update { it.copy(isSaving = false) }
                return@launch
            }

            val newKid = Kid(
                parentId = parentId,
                name = currentState.name,
                ageCategory = ageCategoryId,
                avatarUrl = currentState.selectedAvatarRes!!,
            )

            try {
                kidDao.insert(newKid)
                println("✅ Kid inserted: $newKid")
                _eventFlow.emit(AddProfileEvent.SaveSuccess)
            } catch (e: Exception) {
                Log.e("AddProfileViewModel", e.message.toString(), )
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

}
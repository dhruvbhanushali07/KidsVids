package com.example.kidsvids.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers // <-- Import Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (Constants and SessionState data class are the same)
private const val PREFS_NAME = "KidsVidsSession"
private const val KEY_PARENT_ID = "logged_in_parent_id"
private const val KEY_KID_ID = "selected_kid_id"

data class SessionState(
    val parentId: Int? = null,
    val selectedKidId: Int? = null
)

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState = _sessionState.asStateFlow()

    private val sharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        // This part is correct
        val savedParentId = sharedPreferences.getInt(KEY_PARENT_ID, 0)
        val savedKidId = sharedPreferences.getInt(KEY_KID_ID, 0)

        if (savedParentId != 0) {
            _sessionState.update {
                it.copy(
                    parentId = savedParentId,
                    selectedKidId = if (savedKidId != 0) savedKidId else null
                )
            }
        }
        else{
            _sessionState.value = SessionState()
        }
    }

    fun onParentLogin(parentId: Int) {
        _sessionState.update { it.copy(parentId = parentId, selectedKidId = null) }

        // --- FIX: Use .commit() on a background thread ---
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit()
                .putInt(KEY_PARENT_ID, parentId)
                .remove(KEY_KID_ID)
                .commit() // Use commit() to save synchronously
        }
    }

    fun onKidProfileSelected(kidId: Int) {
        _sessionState.update { it.copy(selectedKidId = kidId) }

        // --- FIX: Use .commit() on a background thread ---
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit()
                .putInt(KEY_KID_ID, kidId)
                .commit() // Use commit() to save synchronously
        }
    }

    fun logout() {
        println("🚀 logout() CALLED")

        // Reset in-memory state
        _sessionState.value = SessionState()

        // Write synchronously (safe for small data)
        sharedPreferences.edit()
            .clear()
            .commit() // <-- commit() blocks until written

        println("✅ After logout, prefs = ${sharedPreferences.all}")
    }



}
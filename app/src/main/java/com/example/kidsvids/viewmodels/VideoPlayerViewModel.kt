package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.VideoDao
import com.example.kidsvids.data.dao.WatchHistoryDao // <-- 1. IMPORT
import com.example.kidsvids.data.entities.Video
import com.example.kidsvids.data.entities.WatchHistory // <-- 2. IMPORT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UiState remains the same
data class VideoPlayerUiState(
    val video: Video? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

// --- 3. UPDATE CONSTRUCTOR ---
class VideoPlayerViewModel(
    private val videoDao: VideoDao,
    private val watchHistoryDao: WatchHistoryDao, // <-- 4. ADD
    private val sessionViewModel: SessionViewModel    // <-- 5. ADD
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState = _uiState.asStateFlow()

    fun loadVideoDetails(videoId: Int?) {
        if (videoId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Video ID not provided.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val video = videoDao.getVideoById(videoId)

            if (video == null) {
                _uiState.update { it.copy(isLoading = false, error = "Video not found.") }
            } else if (video.sourceType != "uploaded") {
                _uiState.update { it.copy(isLoading = false, error = "Only uploaded videos can be played.") }
            } else {
                _uiState.update { it.copy(isLoading = false, video = video) }
                // --- 6. CALL HISTORY FUNCTION ---
                addVideoToHistory(videoId)
            }
        }
    }

    // --- 7. ADD NEW FUNCTION TO SAVE HISTORY ---
    private fun addVideoToHistory(videoId: Int) {
        viewModelScope.launch {
            // Get the currently selected kid ID
            val kidId = sessionViewModel.sessionState.first().selectedKidId
            if (kidId != null) {
                // Create a new WatchHistory record
                // The DAO is set to OnConflictStrategy.REPLACE,
                // so this will insert a new record or update the timestamp of an existing one.
                val historyRecord = WatchHistory(
                    kidId = kidId,
                    videoId = videoId
                    // progressSeconds and lastWatchedAt use their default values
                )
                watchHistoryDao.insertOrUpdate(historyRecord)
            }
        }
    }
}
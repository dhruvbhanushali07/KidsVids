package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.KidBlockedVideoDao
import com.example.kidsvids.data.dao.VideoDao
import com.example.kidsvids.data.entities.KidBlockedVideo
import com.example.kidsvids.data.entities.Video
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Simple UI state for this screen
data class BlockedVideosUiState(
    val isLoading: Boolean = true,
    val videos: List<Video> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class BlockedVideosViewModel(
    private val videoDao: VideoDao,
    private val kidBlockedVideoDao: KidBlockedVideoDao,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockedVideosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeBlockedVideos()
    }

    private fun observeBlockedVideos() {
        viewModelScope.launch {
            // Start by getting the flow of the selected kid ID
            sessionViewModel.sessionState.map { it.selectedKidId }
                .flatMapLatest { kidId ->
                    // When the kidId changes, switch to getting their blocked video IDs
                    if (kidId == null) {
                        flowOf(emptyList()) // If no kid, return an empty list of IDs
                    } else {
                        kidBlockedVideoDao.getBlockedVideoIdsForKid(kidId)
                    }
                }
                .flatMapLatest { videoIds ->
                    // When the list of IDs changes, get the full video details for those IDs
                    if (videoIds.isEmpty()) {
                        flowOf(emptyList()) // If no IDs, return an empty list of Videos
                    } else {
                        videoDao.getVideosByIds(videoIds)
                    }
                }
                .collect { videosList ->
                    // Finally, update the UI state with the list of videos
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            videos = videosList
                        )
                    }
                }
        }
    }

    // Function to unblock a video
    fun unblockVideo(videoId: Int) {
        viewModelScope.launch {
            // Get the current kid ID from the session
            val kidId = sessionViewModel.sessionState.value.selectedKidId ?: return@launch

            // Create the entity object to delete
            val blockedVideo = KidBlockedVideo(kidId = kidId, videoId = videoId)

            // Delete it from the database
            kidBlockedVideoDao.delete(blockedVideo)
            // The UI will update automatically because our `observeBlockedVideos` flow
            // will get the new list of IDs and re-fetch the videos.
        }
    }
}
package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.KidFavoriteDao
import com.example.kidsvids.data.dao.VideoDao
import com.example.kidsvids.data.dao.WatchHistoryDao
import com.example.kidsvids.data.entities.Video
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI State for YourStuffScreen
data class YourStuffUiState(
    val isLoading: Boolean = true,
    val selectedTab: YourStuffTab = YourStuffTab.HISTORY,
    val historyVideos: List<Video> = emptyList(),
    val savedVideos: List<Video> = emptyList(),
    val error: String? = null
)

// Enum to represent the tabs
enum class YourStuffTab { HISTORY, SAVED }

@OptIn(ExperimentalCoroutinesApi::class)
class YourStuffViewModel(
    private val watchHistoryDao: WatchHistoryDao,
    private val kidFavoriteDao: KidFavoriteDao,
    private val videoDao: VideoDao,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(YourStuffUiState())
    val uiState: StateFlow<YourStuffUiState> = _uiState.asStateFlow()

    // Flow for the selected kid ID
    private val selectedKidIdFlow = sessionViewModel.sessionState
        .map { it.selectedKidId }
        .distinctUntilChanged()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            selectedKidIdFlow.flatMapLatest { kidId ->
                if (kidId == null) {
                    // Reset state if no kid selected
                    flowOf(Pair(emptyList<Int>(), emptyList<Int>()))
                } else {
                    // Combine the flows of IDs from history and favorites
                    combine(
                        watchHistoryDao.getHistoryVideoIdsForKid(kidId),
                        kidFavoriteDao.getFavoriteVideoIdsForKid(kidId)
                    ) { historyIds, savedIds ->
                        Pair(historyIds, savedIds)
                    }
                }
            }.flatMapLatest { (historyIds, savedIds) ->
                // Combine the actual Video details based on the collected IDs
                // Fetch all unique IDs needed at once
                val allNeededIds = (historyIds + savedIds).distinct()
                if (allNeededIds.isEmpty()) {
                    flowOf(Pair(emptyList<Video>(), emptyList<Video>())) // No videos needed
                } else {
                    videoDao.getVideosByIds(allNeededIds).map { videos ->
                        // Create maps for quick lookup
                        val videoMap = videos.associateBy { it.id }
                        // Map back the IDs to Video objects
                        val historyVideoList = historyIds.mapNotNull { videoMap[it] }
                        val savedVideoList = savedIds.mapNotNull { videoMap[it] }
                        Pair(historyVideoList, savedVideoList)
                    }
                }
            }.collect { (historyList, savedList) ->
                // Update the UI state with the fetched video lists
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        historyVideos = historyList,
                        savedVideos = savedList,
                        error = if (sessionViewModel.sessionState.value.selectedKidId == null) "No profile selected." else null
                    )
                }
            }
        }
    }

    // Called by the UI when a tab is selected
    fun selectTab(tab: YourStuffTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
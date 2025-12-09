package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsvids.data.dao.*
import com.example.kidsvids.data.entities.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- UI State Data Class (No changes needed) ---
data class HomeUiState(
    val isLoading: Boolean = true,
    val kidName: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val videos: List<Video> = emptyList(),
    val favoritedVideoIds: Set<Int> = emptySet(),
    val error: String? = null
)

class HomeViewModel(
    private val videoDao: VideoDao,
    private val kidDao: KidDao,
    private val categoryDao: CategoryDao,
    private val kidFavoriteDao: KidFavoriteDao,
    private val kidBlockedVideoDao: KidBlockedVideoDao,
    private val sessionViewModel: SessionViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Keep track of active data loading jobs to avoid conflicts
    private var kidDataJob: Job? = null

    init {
        loadCategories()
        observeSelectedKid()
    }

    /** Load all categories once */
    private fun loadCategories() {
        viewModelScope.launch {
            val categoryList = categoryDao.getAllCategoriesList() // <-- Direct assignment
            _uiState.update { it.copy(categories = categoryList) }
        }
    }

    /** Observe the selected kid ID from the session */
    private fun observeSelectedKid() {
        viewModelScope.launch {
            sessionViewModel.sessionState
                .map { it.selectedKidId } // Get only the kid ID
                .distinctUntilChanged() // Only react if it changes
                .collect { kidId ->
                    // Cancel any previous data loading for a different kid
                    kidDataJob?.cancel()
                    if (kidId == null) {
                        // Reset state if no kid is selected
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                kidName = "",
                                videos = emptyList(),
                                favoritedVideoIds = emptySet(),
                                error = "No profile selected."
                            )
                        }
                    } else {
                        // Load all data for the newly selected kid
                        loadContentForKid(kidId)
                    }
                }
        }
    }

    /** Load kid details, videos (based on current filter), and favorites */
    private fun loadContentForKid(kidId: Int) {
        // Start a new job for loading data for this kid
        kidDataJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Start loading

            // Fetch Kid details separately (only needs to be done once per kid selection)
            val kid = kidDao.getKidById(kidId).first() // Assuming DAO returns Flow<Kid?>
            if (kid == null) {
                _uiState.update { it.copy(isLoading = false, error = "Selected profile not found.") }
                return@launch
            }
            _uiState.update { it.copy(kidName = kid.name) }

            // Launch separate collectors for videos and favorites for this kid
            launch { observeVideos(kidId, _uiState.value.selectedCategoryId) }
            launch { observeFavorites(kidId) }
        }
    }

    /** Observe the list of videos for the current kid and category filter */
    private suspend fun observeVideos(kidId: Int, categoryId: Int?) {
        // Assuming videoDao.getVideosForKid returns Flow<List<Video>>
        videoDao.getVideosForKid(kidId, categoryId).collect { videoList ->
            _uiState.update {
                it.copy(
                    isLoading = false, // Consider loading done once videos arrive
                    videos = videoList
                )
            }
        }
    }

    /** Observe the list of favorite video IDs for the current kid */
    private suspend fun observeFavorites(kidId: Int) {
        // Assuming kidFavoriteDao.getFavoriteVideoIdsForKid returns Flow<List<Int>>
        kidFavoriteDao.getFavoriteVideoIdsForKid(kidId).map { it.toSet() }.collect { favIds ->
            _uiState.update { it.copy(favoritedVideoIds = favIds) }
        }
    }

    /** Select category filter and trigger video reload */
    fun onCategorySelected(categoryId: Int?) {
        val currentKidId = sessionViewModel.sessionState.value.selectedKidId
        _uiState.update { it.copy(selectedCategoryId = categoryId) }

        // If a kid is selected, cancel previous video loading and start new
        if (currentKidId != null) {
            kidDataJob?.cancel() // Cancel previous loading (including video/fav observers)
            loadContentForKid(currentKidId) // Reload everything based on new filter
        }
    }

    /** Toggle favorite video */
    fun toggleFavorite(videoId: Int) {
        viewModelScope.launch {
            val kidId = sessionViewModel.sessionState.value.selectedKidId ?: return@launch
            val isFav = _uiState.value.favoritedVideoIds.contains(videoId)

            if (isFav) {
                kidFavoriteDao.delete(kidId, videoId)
            } else {
                kidFavoriteDao.insert(KidFavorite(kidId, videoId))
            }

            // UI will update automatically because observeFavorites is collecting the Flow
        }
    }


    /** Block a video */
    fun blockVideo(videoId: Int) {
        viewModelScope.launch {
            val kidId = sessionViewModel.sessionState.value.selectedKidId ?: return@launch
            kidBlockedVideoDao.insert(KidBlockedVideo(kidId, videoId))
            // UI will update automatically because observeVideos collects the Flow
            // which excludes blocked videos based on the DAO query.
        }
    }
}
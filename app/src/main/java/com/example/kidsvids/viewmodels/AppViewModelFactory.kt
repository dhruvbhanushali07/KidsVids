package com.example.kidsvids.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kidsvids.data.AppDatabase

/**
 * This is our single, unified factory for creating all ViewModels that have dependencies.
 * It takes the dependencies it might need (like the database) in its constructor.
 */
@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(
    private val database: AppDatabase,
    private val sessionViewModel: SessionViewModel? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // A 'when' block checks which ViewModel the system is asking for...
        return when {

            modelClass.isAssignableFrom(SessionViewModel::class.java) -> {
                sessionViewModel as T
            }

            modelClass.isAssignableFrom(SessionViewModel::class.java) -> {
                sessionViewModel as T
            }
            // If the requested ViewModel is LoginViewModel...
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                // ...create a LoginViewModel, passing the dependencies it needs.
                LoginViewModel(database.parentDao(), sessionViewModel!!) as T
            }
            // If the requested ViewModel is SignupViewModel...
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(database.parentDao()) as T
            }
            // If the requested ViewModel is ProfileSelectionViewModel...
            modelClass.isAssignableFrom(ProfileSelectionViewModel::class.java) -> {
                ProfileSelectionViewModel(database.kidDao(), sessionViewModel!!) as T
            }

            modelClass.isAssignableFrom(AddProfileViewModel::class.java) -> {
                AddProfileViewModel(database.kidDao(), sessionViewModel!!) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    videoDao = database.videoDao(),
                    kidDao = database.kidDao(),
                    categoryDao = database.categoryDao(),
                    kidFavoriteDao = database.kidFavoriteDao(), // <-- ADDED
                    kidBlockedVideoDao = database.kidBlockedVideoDao(), // <-- ADDED
                    sessionViewModel = sessionViewModel!!
                ) as T
            }

            modelClass.isAssignableFrom(VideoPlayerViewModel::class.java) -> {
                VideoPlayerViewModel(
                    videoDao = database.videoDao(),
                    watchHistoryDao = database.watchHistoryDao(), // <-- ADDED
                    sessionViewModel = sessionViewModel!!        // <-- ADDED
                ) as T
            }

            modelClass.isAssignableFrom(YourStuffViewModel::class.java) -> {
                YourStuffViewModel(
                    watchHistoryDao = database.watchHistoryDao(),
                    kidFavoriteDao = database.kidFavoriteDao(),
                    videoDao = database.videoDao(),
                    sessionViewModel = sessionViewModel!!
                ) as T
            }

            modelClass.isAssignableFrom(AccountViewModel::class.java) -> {
                AccountViewModel(
                    parentDao = database.parentDao(),
                    kidDao = database.kidDao(),
                    sessionViewModel = sessionViewModel!!
                ) as T
            }

            modelClass.isAssignableFrom(BlockedVideosViewModel::class.java) -> {
                BlockedVideosViewModel(
                    videoDao = database.videoDao(),
                    kidBlockedVideoDao = database.kidBlockedVideoDao(),
                    sessionViewModel = sessionViewModel!!
                ) as T
            }

            modelClass.isAssignableFrom(AdminLoginViewModel::class.java) -> {
                AdminLoginViewModel() as T // No dependencies needed yet
            }
          

            // If we ask for a ViewModel it doesn't know how to create, it will throw an error.
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
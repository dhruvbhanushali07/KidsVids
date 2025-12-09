package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.kidsvids.viewmodels.AppViewModelFactory

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Text
import com.example.kidsvids.viewmodels.SessionViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation


sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Auth and Profile setup (no icons)

    object Login : Screen("login", "Login")
    object Signup : Screen("signup", "Signup")
    object ProfileSelection : Screen("profile_selection", "Profiles")
    object AddProfile : Screen("add_profile", "Add Profile")

    // Group all main app destinations under "main_graph"
    object MainGraph : Screen("main_graph", "Main Graph")

    // Main app screens (with icons for bottom nav)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object YourStuff : Screen("your_stuff", "Your Stuff", Icons.Default.VideoLibrary)
    object Account : Screen("account", "Account", Icons.Default.Person)

    // Video player (no bottom bar)
    object VideoPlayer : Screen("video_player/{videoId}", "Video Player") {
        fun createRoute(videoId: Int) = "video_player/$videoId"
    }

    object BlockedVideos : Screen("blocked_videos", "Blocked Videos")
    object AdminLogin : Screen("admin_login", "Admin Login")
    object AdminDashboard : Screen("admin_dashboard", "Admin Dashboard")
    object ManageVideos : Screen("manage_videos", "Manage Videos")
    object ManageCategories : Screen("manage_categories", "Manage Categories")
    object ManageUsers : Screen("manage_users", "Manage Users")
    object Reports : Screen("reports", "Reports")

    // This route will be for both Adding and Editing
    // We'll pass an optional 'videoId' to it. If it's null, we're adding.
    object AddEditVideo : Screen("add_edit_video", "Add/Edit Video")
}

// 👇 Convenience list for bottom nav
val bottomBarScreens = listOf(
    Screen.Home,
    Screen.YourStuff,
    Screen.Account
)

@Composable
fun AppNavGraph(
    navController: NavHostController,
    factory: AppViewModelFactory,
    sessionViewModel: SessionViewModel
) {

    val sessionState by sessionViewModel.sessionState.collectAsState()

    // --- THIS IS THE KEY ---
    // Decide the start route based on whether a parent is logged in
    val startDestination = if (sessionState.parentId != null) {
        // A parent is logged in.
        // Now check if they have also selected a kid profile.

            "main_graph" // Logged in, but no kid selected, go to Profile Selection
    } else {
        "auth_graph" // No one logged in, start at the Login/Signup flow
    }

    NavHost(navController = navController, startDestination = startDestination) {

        navigation(
            startDestination = Screen.Login.route,
            route = "auth_graph"
        ) {
            composable(Screen.Login.route) { LoginScreen(navController, factory) }
            composable(Screen.Signup.route) { SignupScreen(navController, factory) }
            composable(Screen.AdminLogin.route) {
                AdminLoginScreen(navController, factory)
            }
        }

        navigation(
            startDestination = Screen.ProfileSelection.route,
            route = "main_graph"
        ) {


            composable(Screen.ProfileSelection.route) {
                ProfileSelectionScreen(navController, factory)
            }
            composable(Screen.AddProfile.route) {
                AddProfileScreen(navController, factory)
            }
        }

        // Main app screens
        composable(Screen.Home.route) { HomeScreen(navController, factory) }
        composable(Screen.YourStuff.route) { YourStuffScreen(navController, factory) }
        composable(Screen.Account.route) { /* TODO: Create AccountScreen */ }
        composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(navArgument("videoId") { type = NavType.IntType })
        ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getInt("videoId")
                VideoPlayerScreen(
                    videoId = videoId,
                    navController = navController,
                    factory = factory // <-- Pass the factory
                )
        }

        composable(Screen.Account.route) { AccountScreen(navController, factory) }

        // --- ADD THIS NEW DESTINATION ---
        composable(Screen.BlockedVideos.route) {
            // TODO: Create BlockedVideosScreen.kt
            BlockedVideosScreen(navController, factory)
        }

        composable(Screen.AdminLogin.route) { AdminLoginScreen(navController, factory) }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController, factory)
        }

        composable(Screen.ManageVideos.route) {
            ManageVideoScreen(navController, factory)
        }

        composable(Screen.ManageCategories.route) {
            ManageCategoryScreen(navController, factory)
        }

        composable(Screen.ManageUsers.route) {
            ManageUserScreen(navController, factory)
        }

        composable(Screen.Reports.route) {
            ReportsScreen(navController, factory)
        }
        // TODO: Add composables for, and Reports

        composable(
            route = Screen.AddEditVideo.route + "?videoId={videoId}",
            arguments = listOf(navArgument("videoId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId")?.toIntOrNull()
            // We will create this screen next
             AddEditVideoScreen(navController, factory, videoId)
        }
    }
}


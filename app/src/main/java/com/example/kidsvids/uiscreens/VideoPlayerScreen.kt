package com.example.kidsvids.uiscreens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.VideoPlayerViewModel

@Composable
fun VideoPlayerScreen(
    videoId: Int?,
    navController: NavController,
    factory: AppViewModelFactory
) {
    val videoPlayerViewModel: VideoPlayerViewModel = viewModel(factory = factory)
    val uiState by videoPlayerViewModel.uiState.collectAsState()

    // --- Force Landscape Mode ---
    val context = LocalContext.current
    val activity = context as? Activity
    val originalOrientation = remember { activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }
    // --- Handle Back Press to Exit Landscape ---
    BackHandler {
        activity?.requestedOrientation = originalOrientation
        navController.popBackStack()
    }

    // --- Load Video Details ---
    LaunchedEffect(key1 = videoId) {
        videoPlayerViewModel.loadVideoDetails(videoId)
    }

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Full black background
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(color = Color.White)
            uiState.error != null -> Text("Error: ${uiState.error}", color = Color.White)
            uiState.video != null -> {
                // We have a valid video, play it with ExoPlayer
                ExoPlayerComposable(videoUrl = uiState.video!!.videoUrl)
            }
        }
    }
}

@Composable
fun ExoPlayerComposable(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 1. Create and remember the ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Create a MediaItem from the Cloudinary URL
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // Start playing automatically
        }
    }

    // 2. Handle the player's lifecycle
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                Lifecycle.Event.ON_DESTROY -> exoPlayer.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release() // Make sure to release the player
        }
    }

    // 3. Display the PlayerView
    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
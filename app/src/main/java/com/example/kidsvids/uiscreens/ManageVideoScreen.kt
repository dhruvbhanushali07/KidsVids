package com.example.kidsvids.uiscreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.data.entities.Video
import com.example.kidsvids.uiscreens.Screen
import com.example.kidsvids.viewmodels.AppViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageVideoScreen(
    navController: NavController,
    factory: AppViewModelFactory // Passed for consistency
) {
    // --- Simple State Management ---
    val context = LocalContext.current
    // Get the DAO directly. No ViewModel needed.
    val videoDao = remember { DatabaseProvider.getDatabase(context).videoDao() }

    // Collect the list of videos as state from the Flow.
    // 'initial = emptyList()' provides a default while the data loads.
    val videos by videoDao.getAllVideos().collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Videos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to Add/Edit screen. No ID means "Add New".
                    navController.navigate(Screen.AddEditVideo.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Video")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(videos, key = { it.id }) { video ->
                AdminVideoListItem(
                    video = video,
                    onEdit = {
                        // Navigate to Add/Edit screen, passing the video ID
                        navController.navigate(Screen.AddEditVideo.route + "?videoId=${video.id}")
                    },
                    onDelete = {
                        // Run the delete operation in a coroutine
                        coroutineScope.launch {
                            videoDao.delete(video)
                            // Show a confirmation snackbar
                            snackbarHostState.showSnackbar("Video '${video.title}' deleted")
                        }
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
private fun AdminVideoListItem(
    video: Video,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() } // Click anywhere on the row to edit
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "ID: ${video.id} | Type: ${video.sourceType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Edit Button
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.secondary)
        }

        // Delete Button
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
        }
    }
}
package com.example.kidsvids.uiscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Keep for bottom nav and menu icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kidsvids.R
import com.example.kidsvids.data.entities.Category
import com.example.kidsvids.data.entities.Video
import com.example.kidsvids.uiscreens.Screen // Make sure Screen class is accessible
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.HomeViewModel

// --- Main Screen Composable ---
@Composable
fun HomeScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = { HomeTopBar() }, // Local Top Bar
        bottomBar = { HomeBottomNavigationBar(navController = navController) } // Local Bottom Bar
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) { // Column to stack filter bar and list

            // --- Category Filter Bar ---
            CategoryFilterBar(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelected = homeViewModel::onCategorySelected // Pass VM function
            )

            // --- Content Area (Loading or List) ---
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.videos.isEmpty()) {
                    Text(
                        "No videos found matching the criteria.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(uiState.videos, key = { it.id }) { video ->
                            VideoListItem(
                                video = video,
                                isFavorited = uiState.favoritedVideoIds.contains(video.id),
                                onVideoClick = { videoId ->
                                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                                },
                                onToggleFavorite = { homeViewModel.toggleFavorite(video.id) },
                                onBlockVideo = { homeViewModel.blockVideo(video.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Category Filter Bar Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterBar(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
                label = { Text(category.name) }
            )
        }
    }
}


// --- Top Bar (Defined Locally) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "KidsVids",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// --- Bottom Navigation Bar (Defined Locally) ---
@Composable
fun HomeBottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.YourStuff,
        Screen.Account
    )
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = screen.route == Screen.Home.route, // Highlight Home
                onClick = {
                    if (screen.route != Screen.Home.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

// --- Video List Item with Menu ---
@Composable
fun VideoListItem(
    video: Video,
    isFavorited: Boolean,
    onVideoClick: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onBlockVideo: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            // Image is clickable to play video
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onVideoClick(video.id) }, // Click plays video
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.img)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // Options Menu Button
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isFavorited) "Unsave" else "Save") },
                        onClick = {
                            onToggleFavorite()
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(
                                if (isFavorited) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Block this video") },
                        onClick = {
                            onBlockVideo()
                            menuExpanded = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Block, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}
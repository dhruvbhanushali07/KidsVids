package com.example.kidsvids.uiscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kidsvids.R
import com.example.kidsvids.data.entities.Video // Use the actual Video entity
import com.example.kidsvids.ui.theme.KidsVidsTheme
import com.example.kidsvids.uiscreens.Screen // Make sure Screen class is accessible
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.YourStuffTab
import com.example.kidsvids.viewmodels.YourStuffViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourStuffScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val yourStuffViewModel: YourStuffViewModel = viewModel(factory = factory)
    val uiState by yourStuffViewModel.uiState.collectAsState()

    val tabs = listOf(YourStuffTab.HISTORY, YourStuffTab.SAVED)
    val tabTitles = listOf("History", "Saved")

    Scaffold(
        // Re-using the simple Top Bar from HomeScreen for consistency
        topBar = { HomeTopBar() }, // You'll need to make HomeTopBar public or move it
        // Re-using the Bottom Bar, setting YourStuff as active
        bottomBar = { HomeBottomNavigationBar(navController = navController, currentScreen = Screen.YourStuff) }, // Assuming this exists now
        containerColor = MaterialTheme.colorScheme.background // Simple background
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            // --- TAB ROW ---
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal, // Use enum ordinal for index
                containerColor = MaterialTheme.colorScheme.surface, // Match TopAppBar color
                indicator = { tabPositions ->
                    if (uiState.selectedTab.ordinal < tabPositions.size) {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { yourStuffViewModel.selectTab(tab) },
                        text = {
                            Text(
                                tabTitles[index],
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- VIDEO LIST ---
            val videosToShow = when (uiState.selectedTab) {
                YourStuffTab.HISTORY -> uiState.historyVideos
                YourStuffTab.SAVED -> uiState.savedVideos
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (videosToShow.isEmpty()) {
                    EmptyState(tabName = tabTitles[uiState.selectedTab.ordinal])
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // --- FIX: Ensure items are unique by ID before rendering ---
                        items(videosToShow.distinctBy { it.id }, key = { it.id }) { video ->
                            VideoListItem(
                                video = video,
                                onVideoClick = { videoId ->
                                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- VIDEO LIST ITEM (Should be identical to HomeScreen's version) ---
// Make sure this composable is accessible (e.g., in a common file or copied here)
// It now needs to accept the real 'Video' entity.
@Composable
fun VideoListItem(
    video: Video, // Use the actual Video entity
    onVideoClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVideoClick(video.id) }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            AsyncImage(
                model = video.thumbnailUrl, // Use thumbnailUrl from Video entity
                contentDescription = video.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.img)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Align title and icon nicely
        ) {
            // --- Add this Text composable ---
            Text(
                text = video.title, // Use the title from the video object
                style = MaterialTheme.typography.titleMedium, // Or titleLarge as you had before
                fontWeight = FontWeight.Bold,
                color = Color.White, // As requested before, or use MaterialTheme.colorScheme.onSurface
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // --- End of Text ---

            IconButton(onClick = { /*TODO: Handle More Options Click*/ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.White.copy(alpha = 0.8f) // As requested before, or MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Empty State Composable (No changes needed) ---
@Composable
fun EmptyState(tabName: String) {
    // ... same as before
}

// --- Bottom Navigation Bar ---
// Ensure this is defined (e.g., copied from HomeScreen or in common/AppBars.kt)
// and accepts the currentScreen parameter.
@Composable
fun HomeBottomNavigationBar(navController: NavController, currentScreen: Screen = Screen.YourStuff) {
    // ... same logic as in HomeScreen, but check against currentScreen.route
    val items = listOf(Screen.Home, Screen.YourStuff, Screen.Account)
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentScreen.route == screen.route, // Use currentScreen
                onClick = {
                    if (currentScreen.route != screen.route) {
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
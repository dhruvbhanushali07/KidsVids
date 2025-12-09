package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kidsvids.data.DatabaseProvider
// Make sure all your entities are imported
import com.example.kidsvids.data.entities.Category
import com.example.kidsvids.data.entities.Parent
import com.example.kidsvids.data.entities.Video
import com.example.kidsvids.data.entities.WatchHistory
import com.example.kidsvids.data.entities.KidFavorite
import com.example.kidsvids.data.entities.KidBlockedVideo
import com.example.kidsvids.viewmodels.AppViewModelFactory
import androidx.compose.ui.text.style.TextOverflow // <-- ADD THIS IMPORT
// Simple data class to hold the report results
private data class ReportItem(val title: String, val count: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    // --- State and DAO Setup ---
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDatabase(context) }

    // --- Data Collection ---
    val allVideos by db.videoDao().getAllVideos().collectAsState(initial = emptyList())
    val allHistory by db.watchHistoryDao().getAllWatchHistory().collectAsState(initial = emptyList())
    val allFavorites by db.kidFavoriteDao().getAllFavorites().collectAsState(initial = emptyList())
    val allBlocked by db.kidBlockedVideoDao().getAllBlocked().collectAsState(initial = emptyList())
    // Assuming your CategoryDao has getAllCategories() that returns a Flow
    val allCategories by db.categoryDao().getAllCategoriesFlow().collectAsState(initial = emptyList())
    val allParents by db.parentDao().getAllParents().collectAsState(initial = emptyList())

    // --- Data Processing (Simple Kotlin logic) ---
    val reports = remember(allVideos, allHistory, allFavorites, allBlocked, allCategories, allParents) {

        // 1. Top 5 Most Watched
        val videoIdToTitleMap = allVideos.associate { it.id to it.title }

        // 1. Top 5 Most Watched
        val topWatched = allHistory
            .groupBy { it.videoId }
            // Use the map to get the title, or fall back to the ID if not found
            .map { ReportItem(title = videoIdToTitleMap[it.key] ?: "Video ID: ${it.key}", count = it.value.size) }
            .sortedByDescending { it.count }
            .take(5)

        // 2. Top 5 Most Saved
        val topSaved = allFavorites
            .groupBy { it.videoId }
            .map { ReportItem(title = videoIdToTitleMap[it.key] ?: "Video ID: ${it.key}", count = it.value.size) }
            .sortedByDescending { it.count }
            .take(5)

        // 3. Top 5 Most Blocked
        val topBlocked = allBlocked
            .groupBy { it.videoId }
            .map { ReportItem(title = videoIdToTitleMap[it.key] ?: "Video ID: ${it.key}", count = it.value.size) }
            .sortedByDescending { it.count }
            .take(5)

        // 4. Video Count by Category
        val categoryMap = allCategories.associate { it.id to it.name }
        val countByCategory = allVideos
            .groupBy { it.videoCategory }
            .map { ReportItem(title = categoryMap[it.key] ?: "Unknown Category", count = it.value.size) }
            .sortedByDescending { it.count }

        // 5. Video Count by Source
        val countBySource = allVideos
            .groupBy { it.sourceType }
            .map { ReportItem(title = it.key.replaceFirstChar { c -> c.uppercase() }, count = it.value.size) }

        // 6. New Parent Signups (simple total count for now)
        val parentCount = allParents.size

        // Put them all into a map for the UI
        mapOf(
            "Top 5 Most Watched" to topWatched,
            "Top 5 Most Saved" to topSaved,
            "Top 5 Most Blocked" to topBlocked,
            "Video Count by Category" to countByCategory,
            "Video Count by Source" to countBySource,
            "Total Parent Accounts" to listOf(ReportItem(title = "Total Parents", count = parentCount))
        )
    }

    // --- UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        // --- FIX: Corrected padding ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), // Apply Scaffold padding to the whole list
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp) // Apply inner padding to the content
        ) {
            reports.forEach { (sectionTitle, items) ->
                // Header for the report section
                item {
                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // --- FIX: Check if the items list is empty ---
                if (items.isEmpty()) {
                    item {
                        // Show a special card if the list is empty
                        ReportCardItem(title = "Not enough data for reports", count = 0)
                    }
                } else {
                    // List of items in this report
                    items(items) { item ->
                        ReportCardItem(title = item.title, count = item.count)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCardItem(title: String, count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                // Mute the text color if it's an "empty data" message
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (count == 0) MaterialTheme.colorScheme.onSurfaceVariant else LocalContentColor.current
            )

            // Only show the count number if it's greater than 0
            if (count > 0) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
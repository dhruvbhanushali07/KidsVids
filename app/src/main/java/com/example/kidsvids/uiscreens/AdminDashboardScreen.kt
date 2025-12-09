package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kidsvids.uiscreens.Screen // Make sure Screen is imported
import com.example.kidsvids.viewmodels.AppViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    factory: AppViewModelFactory // Kept for consistency
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    // Go back to the login screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Content Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            AdminMenuButton(
                text = "Manage Videos",
                icon = Icons.Default.VideoLibrary,
                onClick = { navController.navigate(Screen.ManageVideos.route) }
            )
            AdminMenuButton(
                text = "Manage Categories",
                icon = Icons.Default.Category,
                onClick = {  navController.navigate(Screen.ManageCategories.route)  }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("User Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            AdminMenuButton(
                text = "Manage Parent Accounts",
                icon = Icons.Default.People,
                onClick = { navController.navigate(Screen.ManageUsers.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            AdminMenuButton(
                text = "Generate Reports",
                icon = Icons.Default.BarChart,
                // --- UPDATE THIS ONCLICK ---
                onClick = { navController.navigate(Screen.Reports.route) }
            )
        }
    }
}

@Composable
private fun AdminMenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}
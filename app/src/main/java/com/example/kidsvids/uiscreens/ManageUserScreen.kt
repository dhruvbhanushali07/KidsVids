package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.data.entities.Parent
import com.example.kidsvids.viewmodels.AppViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUserScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    // --- State and DAO Setup ---
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Get the DAO directly, no ViewModel needed
    val parentDao = remember { DatabaseProvider.getDatabase(context).parentDao() }

    // Get the list of parents as state. This list will update automatically.
    val parents by parentDao.getAllParents().collectAsState(initial = emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Parent Accounts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(parents, key = { it.id }) { parent ->
                ParentListItem(
                    parent = parent,
                    onStatusChange = { newStatus ->
                        // Simple logic: onStatusChange is called in a coroutine
                        coroutineScope.launch {
                            val updatedParent = parent.copy(isActive = newStatus)
                            parentDao.update(updatedParent)
                            snackbarHostState.showSnackbar("Status updated for ${parent.fullname}")
                        }
                    },
                    onDelete = {
                        coroutineScope.launch {
                            parentDao.delete(parent)
                            snackbarHostState.showSnackbar("Account deleted: ${parent.fullname}")
                        }
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
private fun ParentListItem(
    parent: Parent,
    onStatusChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Parent Info ---
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = parent.fullname,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = parent.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // --- Activate/Deactivate Switch ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Switch(
                checked = parent.isActive,
                onCheckedChange = onStatusChange
            )
            Text(
                text = if (parent.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.labelSmall
            )
        }

        // --- Delete Button ---
        IconButton(
            onClick = onDelete,
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Delete, "Delete Account")
        }
    }
}
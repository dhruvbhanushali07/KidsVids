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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.data.entities.Category
import com.example.kidsvids.viewmodels.AppViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun ManageCategoryScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    // --- State and DAO Setup ---
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Get the DAO directly, no ViewModel needed
    val categoryDao = remember { DatabaseProvider.getDatabase(context).categoryDao() }

    // Get the list of categories as state. This list will update automatically.
    val categories by categoryDao.getAllCategoriesFlow().collectAsState(initial = emptyList())

    // State for the "Add Category" text field
    var newCategoryName by remember { mutableStateOf("") }

    // State to control the Edit dialog
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
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
            // --- "Add New Category" Form ---
            item {
                AddCategoryForm(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    onAddClick = {
                        if (newCategoryName.isNotBlank()) {
                            coroutineScope.launch {
                                // ID 0 tells Room it's a new entry
                                categoryDao.insert(Category(name = newCategoryName))
                                newCategoryName = "" // Clear the text field
                                snackbarHostState.showSnackbar("Category added")
                            }
                        }
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // --- List of Existing Categories ---
            items(categories, key = { it.id }) { category ->
                CategoryListItem(
                    category = category,
                    onEdit = {
                        // Set the category to edit, which triggers the dialog
                        categoryToEdit = category
                    },
                    onDelete = {
                        coroutineScope.launch {
                            categoryDao.delete(category)
                            snackbarHostState.showSnackbar("Category deleted")
                        }
                    }
                )
                Divider()
            }
        }
    }

    // --- Edit Category Dialog ---
    if (categoryToEdit != null) {
        // This state is local to the dialog
        var editName by remember { mutableStateOf(categoryToEdit!!.name) }

        AlertDialog(
            onDismissRequest = { categoryToEdit = null },
            title = { Text("Edit Category") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            // Save the updated category
                            categoryDao.update(categoryToEdit!!.copy(name = editName))
                            categoryToEdit = null // Close the dialog
                            snackbarHostState.showSnackbar("Category updated")
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AddCategoryForm(
    value: String,
    onValueChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("New Category Name") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        IconButton(onClick = onAddClick) {
            Icon(Icons.Default.Add, "Add Category")
        }
    }
}

@Composable
private fun CategoryListItem(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() } // Click row to edit
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.secondary)
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
        }
    }
}
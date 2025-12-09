package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.data.entities.AgeCategory
import com.example.kidsvids.data.entities.Category
import com.example.kidsvids.data.entities.Video
import com.example.kidsvids.viewmodels.AppViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVideoScreen(
    navController: NavController,
    factory: AppViewModelFactory, // We keep this for consistency, though not used
    videoId: Int? // This determines if we are in "Add" or "Edit" mode
) {
    val isEditMode = videoId != null
    val coroutineScope = rememberCoroutineScope()

    // --- Get DAOs directly ---
    val context = LocalContext.current
    val videoDao = remember { DatabaseProvider.getDatabase(context).videoDao() }
    val categoryDao = remember { DatabaseProvider.getDatabase(context).categoryDao() }
    val ageCategoryDao = remember { DatabaseProvider.getDatabase(context).ageCategoryDao() }

    // --- Form State ---
    var title by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var thumbnailUrl by remember { mutableStateOf("") }
    var sourceType by remember { mutableStateOf("uploaded") }
    var status by remember { mutableStateOf("published") }

    // --- Dropdown States ---
    var categories by remember { mutableStateOf(emptyList<Category>()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    var ageCategories by remember { mutableStateOf(emptyList<AgeCategory>()) }
    var selectedAgeCategory by remember { mutableStateOf<AgeCategory?>(null) }
    var ageCategoryMenuExpanded by remember { mutableStateOf(false) }

    // --- Data Loading ---
    // This LaunchedEffect runs once to load data for the form
    LaunchedEffect(key1 = Unit) {
        // Load the dropdown options
        categories = categoryDao.getAllCategoriesList()
        ageCategories = ageCategoryDao.getAllAgeCategoriesList()

        if (isEditMode) {
            // If we are editing, fetch the video's data
            val video = videoDao.getVideoById(videoId!!)
            if (video != null) {
                // Populate all the form fields with the video's data
                title = video.title
                videoUrl = video.videoUrl
                thumbnailUrl = video.thumbnailUrl
                sourceType = video.sourceType
                status = video.status
                selectedCategory = categories.find { it.id == video.videoCategory }
                selectedAgeCategory = ageCategories.find { it.id == video.ageCategory }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Video" else "Add Video") },
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
                    // --- Save Logic ---
                    coroutineScope.launch {
                        if (selectedCategory == null || selectedAgeCategory == null) {
                            // We can't save without a category.
                            // You could show an error message here, but for simplicity,
                            // we'll just prevent the save and the crash.
                            return@launch
                        }

                        val videoToSave = Video(
                            id = videoId ?: 0, // If new, ID is 0 for auto-generate
                            title = title,
                            videoUrl = videoUrl,
                            thumbnailUrl = thumbnailUrl,
                            sourceType = sourceType,
                            status = status,
                            videoCategory = selectedCategory?.id ?: 0,
                            ageCategory = selectedAgeCategory?.id ?: 0
                        )

                        if (isEditMode) {
                            videoDao.update(videoToSave)
                        } else {
                            videoDao.insert(videoToSave)
                        }

                        // Go back to the previous screen
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Video Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text("Video URL or YouTube ID") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = thumbnailUrl,
                onValueChange = { thumbnailUrl = it },
                label = { Text("Thumbnail URL") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Dropdown for Video Category ---
            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Video Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // --- Dropdown for Age Category ---
            ExposedDropdownMenuBox(
                expanded = ageCategoryMenuExpanded,
                onExpandedChange = { ageCategoryMenuExpanded = !ageCategoryMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedAgeCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Age Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageCategoryMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = ageCategoryMenuExpanded,
                    onDismissRequest = { ageCategoryMenuExpanded = false }
                ) {
                    ageCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedAgeCategory = category
                                ageCategoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // --- Simple Dropdowns for Source Type and Status ---
            SimpleDropdown(
                label = "Source Type",
                options = listOf("uploaded", "youtube"),
                selectedOption = sourceType,
                onOptionSelected = { sourceType = it }
            )
            SimpleDropdown(
                label = "Status",
                options = listOf("published", "archived"),
                selectedOption = status,
                onOptionSelected = { status = it }
            )
        }
    }
}

// A simple, reusable dropdown composable for our form
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
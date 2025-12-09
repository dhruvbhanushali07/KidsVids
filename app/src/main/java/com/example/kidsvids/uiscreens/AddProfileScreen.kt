package com.example.kidsvids.uiscreens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidsvids.R // This is now very important
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.ui.theme.KidsVidsTheme
import com.example.kidsvids.viewmodels.AddProfileEvent
import com.example.kidsvids.viewmodels.AddProfileViewModel
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.SessionViewModel
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(navController: NavController, factory: AppViewModelFactory) {
    val sessionViewModel: SessionViewModel = viewModel(factory = factory)
    val database = DatabaseProvider.getDatabase(LocalContext.current)
    val addProfileViewModel: AddProfileViewModel = viewModel(factory = factory)
    val uiState by addProfileViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        addProfileViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddProfileEvent.SaveSuccess -> {
                    navController.navigate(Screen.ProfileSelection.route) {
                        popUpTo(Screen.AddProfile.route) { inclusive = true }
                    }
                }
            }
        }
    }


    // CHANGED: The list now contains local drawable resource IDs (Ints)
    val avatars = listOf(
        R.drawable.disneyelsa,
        R.drawable.disneypo,
        R.drawable.disneybuzz,
        R.drawable.disneybelle,
        R.drawable.disneysimba,
        R.drawable.disneybaymax
    )
    val ageCategories = listOf("Preschool", "Younger", "Older")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... (Name field and Age Category FilterChips are the same) ...
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { addProfileViewModel.onNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Child's Name") },
                leadingIcon = { Icon(Icons.Default.ChildCare, null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Age Category", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                ageCategories.forEach { category ->
                    FilterChip(
                        selected = uiState.selectedAgeCategory == category,
                        onClick = { addProfileViewModel.onAgeCategorySelected(category) },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Choose an Avatar", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(avatars) { avatarRes ->
                    AvatarItem(
                        avatarRes = avatarRes, // Now passing the resource Int
                        isSelected = uiState.selectedAvatarRes == avatarRes,
                        onClick = { addProfileViewModel.onAvatarSelected(avatarRes) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { addProfileViewModel.onSaveClick() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = uiState.name.isNotBlank() && uiState.selectedAgeCategory != null && uiState.selectedAvatarRes != null && !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Save Profile", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AvatarItem(avatarRes: Int, isSelected: Boolean, onClick: () -> Unit) { // CHANGED: parameter is now avatarRes: Int
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(4.dp, borderColor, CircleShape)
            .padding(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // CHANGED: Reverted back to the standard Image composable
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
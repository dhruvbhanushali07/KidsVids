package com.example.kidsvids.uiscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kidsvids.R
import com.example.kidsvids.ui.theme.KidsVidsTheme
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.ProfileSelectionViewModel
import com.example.kidsvids.viewmodels.SessionViewModel
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
// Data class for the UI (this is correct)
data class KidProfile(
    val id: Int,
    val name: String,
    val avatarRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {

    val profileViewModel: ProfileSelectionViewModel = viewModel(factory = factory)
    val sessionViewModel: SessionViewModel = viewModel(factory = factory)
    val uiState by profileViewModel.uiState.collectAsState()

    val context = LocalContext.current

    // Safely convert the database entities to the UI data class
    val profiles = uiState.profiles.map { kid ->
        KidProfile(
            id = kid.id,
            name = kid.name,
            avatarRes = kid.avatarUrl
        )
    }

    ProfileSelectionContent(
        isLoading = uiState.isLoading,
        profiles = profiles,
        onProfileClick = { profileId ->
            sessionViewModel.onKidProfileSelected(profileId)
            navController.navigate(Screen.Home.route)
        },
        onAddProfileClick = {
            navController.navigate(Screen.AddProfile.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectionContent(
    isLoading: Boolean,
    profiles: List<KidProfile>,
    onProfileClick: (Int) -> Unit,
    onAddProfileClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Who's Watching?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    items(profiles) { profile ->
                        ProfileItem(
                            profile = profile,
                            onClick = {
                                onProfileClick(profile.id)
                            }
                        )
                    }
                    item {
                        AddProfileItem(onClick = onAddProfileClick)
                    }
                }
            }
        }
    }
}



@Composable
fun ProfileItem(profile: KidProfile, onClick: () -> Unit) {
    val profileColors = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6),
        Color(0xFFFFD54F), Color(0xFF9575CD), Color(0xFFFFB74D)
    )
    val backgroundColor = profileColors[profile.id % profileColors.size]

    val context = LocalContext.current
    val drawable = context.getDrawable(profile.avatarRes)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            drawable?.let {
                Image(
                    bitmap = it.toBitmap().asImageBitmap(),
                    contentDescription = profile.name,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = profile.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun AddProfileItem(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Profile",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Add Profile",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}



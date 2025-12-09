package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kidsvids.uiscreens.Screen
import com.example.kidsvids.viewmodels.AccountViewModel
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val accountViewModel: AccountViewModel = viewModel(factory = factory)
    val sessionViewModel: SessionViewModel = viewModel(factory = factory)
    val uiState by accountViewModel.uiState.collectAsState()

    // This state controls whether the screen is locked or unlocked
    // rememberSaveable keeps it unlocked if you rotate the screen
    var isUnlocked by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            // Each screen has its own bottom bar
            AccountBottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                // --- SHOW UNLOCKED CONTENT ---
                AccountDetailsView(
                    email = uiState.parentEmail,
                    kidName = uiState.currentKidName,
                    onChangeProfileClicked = {
                        // Navigate back to profile selection
                        navController.navigate(Screen.ProfileSelection.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onBlockedVideosClicked = {
                        navController.navigate("blocked_videos") // You'll need to add this route
                    },
                    onLogoutClicked = {
                        sessionViewModel.logout()
                        // Go back to login and clear the entire app history
                        navController.navigate("auth_graph") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            } else {
                // --- SHOW PIN LOCK ---
                PinEntryView(
                    onPinComplete = { enteredPin ->
                        if (accountViewModel.checkPin(enteredPin)) {
                            isUnlocked = true
                        }
                    }
                )
            }
        }
    }
}

// --- PIN Entry UI ---
@Composable
fun PinEntryView(onPinComplete: (String) -> Unit) {
    var pinValue by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Icon(Icons.Default.Lock, contentDescription = "Locked", modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter your Parent PIN",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This area is for parents only.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = pinValue,
            onValueChange = {
                if (it.length <= 4) {
                    pinValue = it.filter { char -> char.isDigit() }
                    isError = false
                }
            },
            label = { Text("4-Digit PIN") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            isError = isError,
            supportingText = { if (isError) Text("Incorrect PIN. Try again.") }
        )

        // Automatically submit when 4 digits are entered
        LaunchedEffect(pinValue) {
            if (pinValue.length == 4) {
                onPinComplete(pinValue)
                pinValue = "" // Reset field after attempt
            }
        }
    }
}

// --- Unlocked Account Details UI ---
@Composable
fun AccountDetailsView(
    email: String,
    kidName: String,
    onChangeProfileClicked: () -> Unit,
    onBlockedVideosClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Account Info ---
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Logged in as:", style = MaterialTheme.typography.labelMedium)
                Text(email, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Current Profile:", style = MaterialTheme.typography.labelMedium)
                Text(kidName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }

        // --- Action Buttons ---
        Button(onClick = onChangeProfileClicked, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Change Profile")
        }

        Button(onClick = onBlockedVideosClicked, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("View Blocked Videos")
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes logout button to the bottom

        Button(
            onClick = onLogoutClicked,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Log Out")
        }
    }
}

// --- Local Bottom Navigation Bar ---
@Composable
fun AccountBottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.YourStuff, Screen.Account)
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = screen.route == Screen.Account.route, // Highlight Account
                onClick = {
                    if (screen.route != Screen.Account.route) {
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
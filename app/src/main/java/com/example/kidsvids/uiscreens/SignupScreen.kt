package com.example.kidsvids.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kidsvids.data.DatabaseProvider
import com.example.kidsvids.viewmodels.AppViewModelFactory
import com.example.kidsvids.viewmodels.SignupEvent
import com.example.kidsvids.viewmodels.SignupViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val database = DatabaseProvider.getDatabase(LocalContext.current)

    // SignupViewModel doesn't need the SessionViewModel, so we can omit it

    // Use the factory to create the SignupViewModel
    val signupViewModel: SignupViewModel = viewModel(factory = factory)
    val uiState by signupViewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var pinVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiState.genericError) {
        uiState.genericError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            signupViewModel.onErrorMessageShown()
        }
    }

    LaunchedEffect(key1 = Unit) {
        signupViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SignupEvent.SignupSuccess -> {
                    navController.navigate("main_graph") {
                        // Clear the entire auth flow from the back stack
                        popUpTo("auth_graph") { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            AuthHeader()
            Spacer(modifier = Modifier.height(48.dp))

            // --- FULL NAME ---
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { signupViewModel.onFullNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                isError = uiState.fullNameError != null,
                supportingText = { uiState.fullNameError?.let { Text(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- EMAIL ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { signupViewModel.onEmailChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = uiState.emailError != null,
                supportingText = { uiState.emailError?.let { Text(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PASSWORD ---
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { signupViewModel.onPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                },
                isError = uiState.passwordError != null,
                supportingText = { uiState.passwordError?.let { Text(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PARENTAL PIN ---
            OutlinedTextField(
                value = uiState.pin,
                onValueChange = { signupViewModel.onPinChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("4-Digit Parental PIN") },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (pinVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { pinVisible = !pinVisible }) {
                        Icon(imageVector = image, contentDescription = if (pinVisible) "Hide PIN" else "Show PIN")
                    }
                },
                isError = uiState.pinError != null,
                supportingText = { uiState.pinError?.let { Text(it) } }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- CREATE ACCOUNT BUTTON ---
            Button(
                onClick = { signupViewModel.onSignupClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Create Account")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            LoginNavigation(navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LoginNavigation(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Already have an account?", style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Signup.route) { inclusive = true }
            }
        }) {
            Text("Login", fontWeight = FontWeight.Bold)
        }
    }
}
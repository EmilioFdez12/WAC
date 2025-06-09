package com.emi.wac.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emi.wac.R
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.ui.components.auth.AdaptiveSpacer
import com.emi.wac.ui.components.auth.AdaptiveText
import com.emi.wac.ui.components.auth.AuthBackground
import com.emi.wac.ui.components.auth.AuthFormContainer
import com.emi.wac.ui.components.auth.AuthHeader
import com.emi.wac.ui.components.auth.AuthSeparator
import com.emi.wac.ui.components.auth.EmailField
import com.emi.wac.ui.components.auth.ErrorMessage
import com.emi.wac.ui.components.auth.PasswordField
import com.emi.wac.ui.components.auth.PasswordResetDialog
import com.emi.wac.ui.components.common.WACButton
import com.emi.wac.ui.components.common.WACButtonStyle
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.utils.GoogleSignInUtils
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

/**
 * Composable function to display the login screen.
 * Handles user authentication and navigation.
 *
 * @param authRepository Repository for authentication operations
 * @param onLoginSuccess Callback invoked when login is successful
 * @param onNavigateToRegister Callback invoked to navigate to the register screen
 */
@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    var showPasswordResetDialog by remember { mutableStateOf(false) }
    
    // Get screen configuration for adaptive layout
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    
    // Adaptive padding and spacing
    val verticalPadding = when {
        screenHeight < 600.dp -> 16.dp
        screenHeight < 700.dp -> 24.dp
        else -> 40.dp
    }
    
    val horizontalPadding = when {
        screenWidth < 360.dp -> 16.dp
        else -> 24.dp
    }

    val maxWidth = when {
        screenWidth < 360.dp -> 0.88f
        screenWidth < 400.dp -> 0.92f
        screenWidth < 600.dp -> 0.96f
        else -> 0.98f
    }

    // Validation function
    fun validateInputs(): String? {
        return when {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = if (screenHeight < 600.dp) Arrangement.Top else Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AuthHeader(primaryColor = PrimaryRed)
            
            // Add adaptive spacer for small screens
            if (screenHeight < 600.dp) {
                AdaptiveSpacer()
            }

            // Form
            AuthFormContainer {
                // Email field
                EmailField(
                    email = email,
                    onEmailChange = { email = it },
                    accentColor = PrimaryRed
                )

                AdaptiveSpacer()

                // Password field
                PasswordField(
                    password = password,
                    onPasswordChange = { password = it },
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword },
                    accentColor = PrimaryRed
                )

                // Error message
                ErrorMessage(errorMessage)

                AdaptiveSpacer()

                // Login button
                WACButton(
                    text = "CONTINUE",
                    onClick = {
                        errorMessage = validateInputs()
                        scope.launch {
                            try {
                                val result = authRepository.signInWithEmail(email, password)
                                result.onSuccess {
                                    onLoginSuccess()
                                }.onFailure { e ->
                                    errorMessage = when (e) {
                                        is FirebaseAuthInvalidUserException -> "This account does not exist"
                                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password"
                                        else -> e.message
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message
                            }
                        }
                    },
                    style = WACButtonStyle.PRIMARY,
                    gradientColors = listOf(PrimaryRed, PrimaryRed),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth(maxWidth)
                )

                AuthSeparator()

                // Google Sign-In button
                WACButton(
                    text = "Continue with Google",
                    onClick = {
                        errorMessage = null
                        GoogleSignInUtils.doGoogleSignIn(
                            context = context,
                            scope = scope,
                            launcher = launcher,
                            login = {
                                onLoginSuccess()
                            }
                        )
                    },
                    style = WACButtonStyle.PRIMARY,
                    gradientColors = listOf(Color.White, PrimaryWhite),
                    textColor = Color.Black,
                    iconRes = R.drawable.google,
                    modifier = Modifier.fillMaxWidth(0.96f)
                )

                AdaptiveSpacer()

                // Links
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AdaptiveText(
                            text = "Don't have an account? ",
                            color = PrimaryWhite,
                            onClick = { onNavigateToRegister() }
                        )
                        AdaptiveText(
                            text = "Sign Up",
                            color = PrimaryRed,
                            onClick = { onNavigateToRegister() }
                        )
                    }

                    AdaptiveSpacer()

                    AdaptiveText(
                        text = "Forgot your password?",
                        color = PrimaryRed,
                        onClick = { showPasswordResetDialog = true }
                    )

                    AdaptiveSpacer()

                    PasswordResetDialog(
                        authRepository,
                        showPasswordResetDialog,
                        onDismiss = { showPasswordResetDialog = false }
                    )
                }
            }
        }
    }
}
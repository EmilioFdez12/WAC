package com.emi.wac.ui.screens

import android.util.Patterns
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
import com.emi.wac.ui.components.auth.EmailConfirmationDialog
import com.emi.wac.ui.components.auth.EmailField
import com.emi.wac.ui.components.auth.ErrorMessage
import com.emi.wac.ui.components.auth.PasswordField
import com.emi.wac.ui.components.auth.UsernameField
import com.emi.wac.ui.components.common.WACButton
import com.emi.wac.ui.components.common.WACButtonStyle
import com.emi.wac.ui.theme.PrimaryBlue
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.utils.GoogleSignInUtils
import kotlinx.coroutines.launch

/**
 * Composable function to display the register screen.
 * Handles user registration and navigation.
 *
 * @param authRepository Repository for authentication operations
 * @param onRegisterSuccess Callback invoked when registration is successful
 * @param onNavigateToLogin Callback invoked to navigate to the login screen
 */
@Composable
fun RegisterScreen(
    authRepository: AuthRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }
    var verificationSent by remember { mutableStateOf(false) }
    var verificationErrorMessage by remember { mutableStateOf<String?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    // Get screen configuration for adaptive layout
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

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
            displayName.length < 3 -> "Username must be at least 3 characters long"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            password.length < 6 -> "Password must be at least 6 characters long"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            password != confirmPassword -> "Passwords do not match"
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
            AuthHeader(primaryColor = PrimaryBlue)

            // Add adaptive spacer for small screens
            if (screenHeight < 600.dp) {
                AdaptiveSpacer()
            }

            // Form
            AuthFormContainer {
                // Username field
                UsernameField(
                    username = displayName,
                    onUsernameChange = { displayName = it },
                    accentColor = PrimaryBlue
                )

                // Email field
                EmailField(
                    email = email,
                    onEmailChange = { email = it },
                    accentColor = PrimaryBlue
                )

                // Password field
                PasswordField(
                    password = password,
                    onPasswordChange = { password = it },
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword },
                    accentColor = PrimaryBlue
                )

                // Confirm Password field
                PasswordField(
                    password = confirmPassword,
                    onPasswordChange = { confirmPassword = it },
                    showPassword = showConfirmPassword,
                    onTogglePasswordVisibility = { showConfirmPassword = !showConfirmPassword },
                    label = "Confirm Password",
                    accentColor = PrimaryBlue
                )

                // Error message
                ErrorMessage(errorMessage)

                AdaptiveSpacer()

                // Register button
                WACButton(
                    text = "REGISTER",
                    onClick = {
                        errorMessage = validateInputs()
                        if (errorMessage == null) {
                            scope.launch {
                                try {
                                    // Proceed with registration
                                    val result = authRepository.createUserWithEmail(
                                        email,
                                        password,
                                        displayName
                                    )
                                    result.onSuccess {
                                        val verificationResult =
                                            authRepository.sendEmailVerification()
                                        verificationResult.onSuccess {
                                            verificationSent = true
                                            showVerificationDialog = true
                                        }.onFailure { e ->
                                            verificationErrorMessage =
                                                "Could not send verification email: ${e.message}"
                                            showVerificationDialog = true
                                        }
                                    }.onFailure { e ->
                                        errorMessage = when {
                                            e.message?.contains("email already exists") == true ->
                                                "An account with this email already exists"

                                            else -> e.message ?: "Registration failed"
                                        }
                                    }
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Registration failed"
                                }
                            }
                        }
                    },
                    style = WACButtonStyle.PRIMARY,
                    gradientColors = listOf(PrimaryBlue, PrimaryBlue),
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
                            login = { onRegisterSuccess() }
                        )
                    },
                    style = WACButtonStyle.PRIMARY,
                    gradientColors = listOf(Color.White, Color.White),
                    textColor = Color.Black,
                    iconRes = R.drawable.google,
                    modifier = Modifier.fillMaxWidth(maxWidth)
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
                            text = "Already have an account? ",
                            color = PrimaryWhite,
                            onClick = { onNavigateToLogin() }
                        )
                        AdaptiveText(
                            text = "Log In",
                            color = PrimaryBlue,
                            onClick = { onNavigateToLogin() }
                        )
                    }
                }
            }
        }

        EmailConfirmationDialog(
            showDialog = showVerificationDialog,
            verificationSent = verificationSent,
            email = email,
            verificationErrorMessage = verificationErrorMessage,
            onDismiss = {
                showVerificationDialog = false
                onNavigateToLogin()
            }
        )
    }
}
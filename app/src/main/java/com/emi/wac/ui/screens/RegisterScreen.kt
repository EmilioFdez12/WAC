package com.emi.wac.ui.screens

import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.R
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.data.repository.AuthRepository
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
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AuthHeader(primaryColor = PrimaryBlue)

            // Form
            AuthFormContainer {
                // Username field
                UsernameField(
                    username = displayName,
                    onUsernameChange = { displayName = it },
                    accentColor = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email field
                EmailField(
                    email = email,
                    onEmailChange = { email = it },
                    accentColor = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                PasswordField(
                    password = password,
                    onPasswordChange = { password = it },
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword },
                    accentColor = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password field
                PasswordField(
                    password = confirmPassword,
                    onPasswordChange = { confirmPassword = it },
                    showPassword = showConfirmPassword,
                    onTogglePasswordVisibility = { showConfirmPassword = !showConfirmPassword },
                    label = "Confirm Password",
                    accentColor = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                ErrorMessage(errorMessage)

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
                    modifier = Modifier.fillMaxWidth(0.96f)
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
                    modifier = Modifier.fillMaxWidth(0.96f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = PrimaryWhite,
                            fontFamily = LEXENDREGULAR,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { onNavigateToLogin() }
                                .padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Log In",
                            color = PrimaryBlue,
                            fontFamily = LEXENDREGULAR,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { onNavigateToLogin() }
                                .padding(bottom = 4.dp)
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
                if (verificationSent) {
                    onRegisterSuccess()
                }
            }
        )
    }
}
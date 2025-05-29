package com.emi.wac.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.R
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.ui.components.auth.*
import com.emi.wac.ui.components.common.WACButton
import com.emi.wac.ui.components.common.WACButtonStyle
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.utils.GoogleSignInUtils
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

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
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AuthHeader(primaryColor = PrimaryRed)

            // Form
            AuthFormContainer {
                // Email field
                EmailField(
                    email = email,
                    onEmailChange = { email = it },
                    accentColor = PrimaryRed
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(32.dp))

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
                                        is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo electrónico"
                                        is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta"
                                        else -> "Error al iniciar sesión: ${e.message}"
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error al iniciar sesión: ${e.message}"
                            }
                        }
                    },
                    style = WACButtonStyle.PRIMARY,
                    gradientColors = listOf(PrimaryRed, PrimaryRed),
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

                Spacer(modifier = Modifier.height(28.dp))

                // Links
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            color = PrimaryWhite,
                            fontFamily = LEXENDREGULAR,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { onNavigateToRegister() }
                        )
                        Text(
                            text = "Sign Up",
                            color = PrimaryRed,
                            fontFamily = LEXENDREGULAR,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { onNavigateToRegister() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Forgot your password?",
                        fontFamily = LEXENDREGULAR,
                        color = PrimaryRed,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { /* TODO: Navigate to password recovery */ }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
package com.emi.wac.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.R
import com.emi.wac.common.Constants.LEXENDBLACK
import com.emi.wac.common.Constants.LEXENDBOLD
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.common.Constants.backgroundImages
import com.emi.wac.data.repository.AuthRepository
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

    // Select a random image only once when the composable is first created
    val randomImage by remember { mutableIntStateOf(backgroundImages.random()) }
    val backgroundPainter = rememberAsyncImagePainter(model = randomImage)

    // Validation function
    fun validateInputs(): String? {
        return when {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = backgroundPainter,
            contentDescription = "Login Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "WELCOME TO",
                    fontSize = 44.sp,
                    fontFamily = LEXENDBLACK,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 4.dp)
                        .background(
                            Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "WAC",
                        fontSize = 56.sp,
                        fontFamily = LEXENDBLACK,
                        color = PrimaryRed
                    )
                }

                Text(
                    text = "\"WE ARE CHECKING\"",
                    fontSize = 16.sp,
                    color = PrimaryRed,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF303030).copy(alpha = 0.5f),
                                Color.Black,
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email field
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                "Email",
                                fontSize = 18.sp,
                                fontFamily = LEXENDREGULAR
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = PrimaryWhite,
                            unfocusedTextColor = PrimaryWhite,
                            focusedLabelColor = Color.LightGray,
                            unfocusedLabelColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = PrimaryRed,
                            unfocusedIndicatorColor = PrimaryWhite.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field with show/hide toggle
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                "Password",
                                fontSize = 18.sp,
                                fontFamily = LEXENDREGULAR
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    painter = painterResource(if (showPassword) R.drawable.eye_closed else R.drawable.eye),
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.LightGray,
                            unfocusedLabelColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = PrimaryRed,
                            unfocusedIndicatorColor = PrimaryWhite.copy(alpha = 0.5f)
                        )
                    )

                    // Show error message if exists
                    errorMessage?.let { message ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = message,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontFamily = LEXENDREGULAR,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

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

                    Separator()

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
                        style = WACButtonStyle.SECONDARY,
                        gradientColors = listOf(Color.White, Color.White),
                        textColor = Color.Black,
                        iconRes = R.drawable.google,
                        modifier = Modifier.fillMaxWidth(0.96f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Links
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Don't have an account? ",
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .clickable { onNavigateToRegister() }
                            )
                            Text(
                                text = "Sign Up",
                                color = PrimaryRed,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .clickable { onNavigateToRegister() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Forgot your password?",
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
}

@Composable
private fun Separator() {
    Text(
        text = "or",
        color = Color.White,
        fontFamily = LEXENDBOLD,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}
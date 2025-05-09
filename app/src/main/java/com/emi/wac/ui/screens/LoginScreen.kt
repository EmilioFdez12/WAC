package com.emi.wac.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.R
import com.emi.wac.common.Constants.LEXENDBLACK
import com.emi.wac.common.Constants.LEXENDBOLD
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.common.Constants.backgroundImages
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.ui.components.login.CustomButton
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.utils.GoogleSignInUtils
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    // Selects a random image
    val randomImage by remember { mutableIntStateOf(backgroundImages.random()) }
    val backgroundPainter = rememberAsyncImagePainter(model = randomImage)

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
                        modifier = Modifier
                            .fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password field
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
                        modifier = Modifier
                            .fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomButton(
                        text = "CONTINUE",
                        gradientColors = listOf(PrimaryRed, PrimaryRed),
                        textColor = Color.White,
                        onClick = {
                            errorMessage = null
                            scope.launch {
                                try {
                                    authRepository.signInWithEmail(email, password)
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Login failed"
                                }
                            }
                        }
                    )

                    Separator()

                    // Google Sign-In button
                    CustomButton(
                        text = "Continue with Google",
                        icon = R.drawable.google,
                        gradientColors = listOf(Color.White, Color.White),
                        textColor = Color.Black,
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
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Links
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Don't have an account? Sign Up",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { /* TODO: Navegar a Sign Up */ }
                                .padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Forgot your password?",
                            color = PrimaryRed,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { /* TODO: Navegar a recuperación de contraseña */ }
                        )
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
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
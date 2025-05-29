package com.emi.wac.ui.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.ui.theme.PrimaryRed
import kotlinx.coroutines.launch

/**
 * Dialog to show password reset
 */
@Composable
fun PasswordResetDialog(
    authRepository: AuthRepository,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    var resetEmail by remember { mutableStateOf("") }
    var resetEmailSent by remember { mutableStateOf(false) }
    var resetErrorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                resetEmailSent = false
                resetErrorMessage = null
            },
            title = { Text("Reset Password") },
            text = {
                Column {
                    if (resetEmailSent) {
                        Text("Password reset email sent. Please check your inbox.")
                    } else {
                        Text("Enter your email address and we'll send you a link to reset your password.")
                        Spacer(modifier = Modifier.height(16.dp))
                        EmailField(
                            email = resetEmail,
                            onEmailChange = { resetEmail = it },
                            accentColor = PrimaryRed
                        )
                        resetErrorMessage?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (resetEmailSent) {
                            onDismiss()
                            resetEmailSent = false
                            resetErrorMessage = null
                        } else {
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(resetEmail)
                                    .matches()
                            ) {
                                resetErrorMessage = "Please enter a valid email address"
                            } else {
                                scope.launch {
                                    try {
                                        val result =
                                            authRepository.sendPasswordResetEmail(resetEmail)
                                        result.onSuccess {
                                            resetEmailSent = true
                                            resetErrorMessage = null
                                        }.onFailure { e ->
                                            resetErrorMessage = "Error: ${e.message}"
                                        }
                                    } catch (e: Exception) {
                                        resetErrorMessage = "Error: ${e.message}"
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text(if (resetEmailSent) "OK" else "Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        resetEmailSent = false
                        resetErrorMessage = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
package com.emi.wac.ui.components.auth

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign

@Composable
fun EmailConfirmationDialog(
    showDialog: Boolean,
    verificationSent: Boolean,
    email: String,
    verificationErrorMessage: String?,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Email Verification") },
            text = {
                Text(
                    text = when {
                        verificationSent ->
                            "A verification email has been sent to $email. Please check your inbox and verify your email address before logging in."
                        verificationErrorMessage != null ->
                            verificationErrorMessage
                        else ->
                            "Could not send verification email. Please try again later."
                    },
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}
package com.emi.wac.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.common.Constants.LEXENDBLACK
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.ui.components.common.BaseCard
import com.emi.wac.ui.components.common.WACButton
import com.emi.wac.ui.components.common.WACButtonStyle
import com.emi.wac.ui.theme.PrimaryRed

/**
 * Composable that displays a logout button and a confirmation dialog for signing out.
 *
 * @param showLogoutDialog Whether to show the logout confirmation dialog
 * @param onShowLogoutDialog Callback to toggle the visibility of the logout dialog
 * @param onLogout Callback to execute when the user confirms logout
 * @param modifier Optional modifier for the composable layout
 */
@Composable
fun LogoutSection(
    showLogoutDialog: Boolean,
    onShowLogoutDialog: (Boolean) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Card containing the logout button with a gradient background
    BaseCard(
        modifier = modifier.padding(vertical = 16.dp),
        gradientColors = listOf(
            Color(0xFF404040),
            Color(0xFF151515),
            Color(0xFF151515)
        ),
        cornerRadius = 8.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Button to trigger the logout confirmation dialog
            WACButton(
                text = "Logout",
                onClick = { onShowLogoutDialog(true) },
                style = WACButtonStyle.PRIMARY,
                gradientColors = listOf(PrimaryRed, Color(0xFFB71C1C))
            )
        }
    }

    // Confirmation dialog shown when the user clicks the logout button
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { onShowLogoutDialog(false) },
            title = {
                Text(
                    text = "Log Out",
                    fontSize = 20.sp,
                    fontFamily = LEXENDBLACK,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out?",
                    fontSize = 16.sp,
                    fontFamily = LEXENDREGULAR,
                    color = Color.White
                )
            },
            confirmButton = {
                // Button to confirm logout action
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRed
                    )
                ) {
                    Text(
                        text = "OK",
                        color = Color.White,
                        fontFamily = LEXENDBLACK
                    )
                }
            },
            dismissButton = {
                // Button to cancel and dismiss the dialog
                Button(
                    onClick = { onShowLogoutDialog(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    )
                ) {
                    Text(
                        text = "CANCEL",
                        color = Color.White,
                        fontFamily = LEXENDBLACK
                    )
                }
            },
            containerColor = Color(0xFF252525),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}
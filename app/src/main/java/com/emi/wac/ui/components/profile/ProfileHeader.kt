package com.emi.wac.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.emi.wac.common.Constants.LEXENDBLACK
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.data.model.auth.User
import com.emi.wac.ui.components.common.BaseCard
import com.emi.wac.ui.theme.PrimaryBlue

/**
 * Composable function to display the header of the profile screen
 * It shows the user's profile picture and name
 *
 * @param currentUser The current user
 * @param modifier The modifier to be applied to the composable
 */
@Composable
fun ProfileHeader(
    currentUser: User?,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier.padding(8.dp),
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
            // Profile picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                currentUser?.photoUrl?.let { photoUrl ->
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } ?: Text(
                    text = currentUser?.displayName?.firstOrNull()?.uppercase() ?: "U",
                    color = PrimaryBlue,
                    fontSize = 48.sp,
                    fontFamily = LEXENDBLACK,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre de usuario
            Text(
                text = currentUser?.displayName ?: "Usuario",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = LEXENDBLACK,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(PrimaryBlue, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            Text(
                text = currentUser?.email ?: "",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = LEXENDREGULAR,
                textAlign = TextAlign.Center
            )
        }
    }
}
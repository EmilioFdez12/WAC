package com.emi.wac.ui.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.emi.wac.ui.theme.PrimaryWhite

@Composable
fun AuthBackground(
    content: @Composable () -> Unit
) {
    val randomImage by remember { mutableIntStateOf(backgroundImages.random()) }
    val backgroundPainter = rememberAsyncImagePainter(model = randomImage)
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = backgroundPainter,
            contentDescription = "Auth Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        content()
    }
}

/**
 * Header
 */
@Composable
fun AuthHeader(
    primaryColor: Color
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
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 2.dp)
        ) {
            Text(
                text = "WAC",
                fontSize = 56.sp,
                fontFamily = LEXENDBLACK,
                color = primaryColor
            )
        }


        Text(
            text = "WE ARE CHECKING",
            fontSize = 18.sp,
            color = PrimaryWhite,
            fontFamily = LEXENDBLACK,
            modifier = Modifier
                .padding(top = 16.dp)
                .background(primaryColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Container fot authentication forms
 */
@Composable
fun AuthFormContainer(
    content: @Composable () -> Unit
) {
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
            content()
        }
    }
}

/**
 * Text field for email
 */
@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    accentColor: Color
) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
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
            focusedIndicatorColor = accentColor,
            unfocusedIndicatorColor = PrimaryWhite.copy(alpha = 0.5f)
        )
    )
}

/**
 * Text field for username
 */
@Composable
fun UsernameField(
    username: String,
    onUsernameChange: (String) -> Unit,
    accentColor: Color
) {
    TextField(
        value = username,
        onValueChange = onUsernameChange,
        label = {
            Text("Username", fontSize = 18.sp, fontFamily = LEXENDREGULAR)
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            focusedTextColor = PrimaryWhite,
            unfocusedTextColor = PrimaryWhite,
            focusedLabelColor = Color.LightGray,
            unfocusedLabelColor = Color.LightGray,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = accentColor,
            unfocusedIndicatorColor = PrimaryWhite.copy(alpha = 0.5f)
        )
    )
}

/**
 * Password field with toggle to show/hide
 */
@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    label: String = "Password",
    accentColor: Color
) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(label, fontSize = 18.sp, fontFamily = LEXENDREGULAR)
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onTogglePasswordVisibility) {
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
            focusedIndicatorColor = accentColor,
            unfocusedIndicatorColor = PrimaryWhite.copy(alpha = 0.5f)
        )
    )
}

/**
 * Separator
 */
@Composable
fun AuthSeparator() {
    Text(
        text = "or",
        color = Color.White,
        fontFamily = LEXENDBOLD,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

/**
 * Message to authentication form
 */
@Composable
fun ErrorMessage(message: String?) {
    message?.let {
        Text(
            text = it,
            color = Color.Red,
            fontSize = 14.sp,
            fontFamily = LEXENDREGULAR,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )
    }
}
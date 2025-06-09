package com.emi.wac.ui.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val headerFontSize = when {
        screenWidth < 360.dp -> 28.sp
        screenWidth < 400.dp -> 36.sp
        screenWidth < 600.dp -> 44.sp
        else -> 56.sp
    }

    val wacFontSize = when {
        screenWidth < 360.dp -> 32.sp
        screenWidth < 400.dp -> 44.sp
        screenWidth < 600.dp -> 56.sp
        else -> 64.sp
    }

    val bodyLargeFont = when {
        screenWidth < 360.dp -> 10.sp
        screenWidth < 400.dp -> 12.sp
        screenWidth < 600.dp -> 18.sp
        else -> 20.sp
    }

    val padding = when {
        screenWidth < 360.dp -> 0.dp
        screenWidth < 400.dp -> 2.dp
        screenWidth < 600.dp -> 4.dp
        else -> 8.dp
    }

    val paddingHorizontal = when {
        screenWidth < 360.dp -> 2.dp
        screenWidth < 400.dp -> 4.dp
        screenWidth < 600.dp -> 8.dp
        else -> 12.dp
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "WELCOME TO",
            fontSize = headerFontSize,
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
                fontSize = wacFontSize,
                fontFamily = LEXENDBLACK,
                color = primaryColor
            )
        }


        Text(
            text = "WE ARE CHECKING",
            fontSize = bodyLargeFont,
            color = PrimaryWhite,
            fontFamily = LEXENDBLACK,
            modifier = Modifier
                .padding(top = 16.dp)
                .background(primaryColor, RoundedCornerShape(8.dp))
                .padding(horizontal = paddingHorizontal, vertical = padding)
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive padding based on screen height
    val containerPadding = when {
        screenWidth < 320.dp -> 4.dp
        screenWidth < 400.dp -> 8.dp
        screenWidth < 600.dp -> 16.dp
        else -> 18.dp
    }

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
            .padding(containerPadding)
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
 * Adaptive spacer based on screen size
 */
@Composable
fun AdaptiveSpacer() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive padding based on screen height
    val height = when {
        screenWidth < 320.dp -> 4.dp
        screenWidth < 400.dp -> 8.dp
        screenWidth < 600.dp -> 20.dp
        else -> 24.dp
    }

    Spacer(modifier = Modifier.height(height))
}

/**
 * Adaptive text for links
 */
@Composable
fun AdaptiveText(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val fontSize = when {
        screenWidth < 360.dp -> 10.sp
        screenWidth < 400.dp -> 12.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    Text(
        text = text,
        color = color,
        fontFamily = LEXENDREGULAR,
        fontSize = fontSize,
        modifier = modifier.clickable { onClick() }
    )
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val bodyLargeFont = when {
        screenWidth < 360.dp -> 8.sp
        screenWidth < 400.dp -> 10.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    // Adaptive height for text fields
    val textFieldHeight = when {
        screenWidth < 360.dp -> 48.dp
        screenWidth < 400.dp -> 52.dp
        screenWidth < 600.dp -> 56.dp
        else -> 60.dp
    }

    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = {
            Text(
                "Email",
                fontSize = bodyLargeFont,
                fontFamily = LEXENDREGULAR
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(textFieldHeight),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val bodyLargeFont = when {
        screenWidth < 360.dp -> 8.sp
        screenWidth < 400.dp -> 10.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    // Adaptive height for text fields
    val textFieldHeight = when {
        screenWidth < 360.dp -> 48.dp
        screenWidth < 400.dp -> 52.dp
        screenWidth < 600.dp -> 56.dp
        else -> 60.dp
    }

    TextField(
        value = username,
        onValueChange = onUsernameChange,
        label = {
            Text("Username", fontSize = bodyLargeFont, fontFamily = LEXENDREGULAR)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(textFieldHeight),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val bodyLargeFont = when {
        screenWidth < 360.dp -> 8.sp
        screenWidth < 400.dp -> 10.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    // Adaptive height for text fields
    val textFieldHeight = when {
        screenWidth < 360.dp -> 48.dp
        screenWidth < 400.dp -> 52.dp
        screenWidth < 600.dp -> 56.dp
        else -> 60.dp
    }

    // Adaptive icon size
    val iconSize = when {
        screenWidth < 360.dp -> 18.dp
        screenWidth < 400.dp -> 20.dp
        else -> 24.dp
    }

    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(label, fontSize = bodyLargeFont, fontFamily = LEXENDREGULAR)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(textFieldHeight),
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        trailingIcon = {
            IconButton(
                onClick = onTogglePasswordVisibility,
                modifier = Modifier.size(iconSize + 8.dp)
            ) {
                Icon(
                    painter = painterResource(if (showPassword) R.drawable.eye_closed else R.drawable.eye),
                    contentDescription = if (showPassword) "Hide password" else "Show password",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
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
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val padding = when {
        screenWidth < 360.dp -> 2.dp
        screenWidth < 400.dp -> 4.dp
        screenWidth < 600.dp -> 12.dp
        else -> 20.dp
    }

    val fontSize = when {
        screenWidth < 360.dp -> 8.sp
        screenWidth < 400.dp -> 10.sp
        screenWidth < 600.dp -> 14.sp
        else -> 20.sp
    }

    Text(
        text = "or",
        color = Color.White,
        fontFamily = LEXENDBOLD,
        fontSize = fontSize,
        modifier = Modifier.padding(vertical = padding)
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
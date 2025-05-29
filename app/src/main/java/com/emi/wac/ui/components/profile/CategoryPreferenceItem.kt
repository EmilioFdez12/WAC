package com.emi.wac.ui.components.profile

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.common.Constants
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.common.Constants.LEXENDREGULAR
import com.emi.wac.data.model.drivers.Driver

/**
 * Composable function to display a category preference item
 * You can toggle the switch to enable or disable notifications
 * You can select a favourite driver
 *
 * @param categoryName The name of the category
 * @param isEnabled Whether notifications are enabled for this category
 * @param favoriteDriver The name of the favourite driver for this category
 * @param categoryColor The color of the category
 * @param isDropdownExpanded Whether the dropdown menu is expanded
 * @param availableDrivers The list of available drivers for this category
 * @param onToggleEnabled Callback to be invoked when the switch is toggled
 * @param onExpandDropdown Callback to be invoked when the dropdown is expanded
 * @param onDriverSelected Callback to be invoked when a driver is selected
 */
@Composable
fun CategoryPreferenceItem(
    categoryName: String,
    isEnabled: Boolean,
    favoriteDriver: String,
    categoryColor: Color,
    isDropdownExpanded: Boolean,
    availableDrivers: List<Driver>,
    onToggleEnabled: (Boolean) -> Unit,
    onExpandDropdown: () -> Unit,
    onDriverSelected: (String) -> Unit
) {
    // Animated rotation of the arrow
    val rotationState by animateFloatAsState(
        targetValue = if (isDropdownExpanded) 180f else 0f,
        label = "rotation"
    )
    // Defines the POST_NOTIFICATIONS permission required to send notifications
    val postNotifications = android.Manifest.permission.POST_NOTIFICATIONS
    val context = LocalContext.current
    // Asks for notifications permission
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onToggleEnabled(isGranted)
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "It is required to allow notifications to receive alerts",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF252525), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${Constants.ASSETS}/logos/$categoryName.png")
                    .crossfade(true)
                    .build(),
                contentDescription = "$categoryName Logo",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = when (categoryName) {
                    CATEGORY_F1 -> "Formula 1"
                    CATEGORY_MOTOGP -> "MotoGP"
                    CATEGORY_INDYCAR -> "IndyCar"
                    else -> categoryName
                },
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = LEXENDREGULAR,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isEnabled,
                onCheckedChange = { checked ->
                    if (checked) {
                        // If user turns on notifications, check if we need to request permission
                        val permissionState = ContextCompat.checkSelfPermission(
                            context, postNotifications
                        )
                        if (permissionState != PackageManager.PERMISSION_GRANTED) {
                            // Ask for permission
                            launcher.launch(postNotifications)
                        } else {
                            // We have permission, enable notifications
                            onToggleEnabled(true)
                        }
                    } else {
                        onToggleEnabled(false)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = categoryColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }

        AnimatedVisibility(visible = isEnabled) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Favourite Driver",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = LEXENDREGULAR
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF303030), RoundedCornerShape(4.dp))
                            .clickable(onClick = onExpandDropdown)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = favoriteDriver.ifEmpty { "Choose driver" },
                            color = if (favoriteDriver.isEmpty()) Color.Gray else Color.White,
                            fontSize = 16.sp,
                            fontFamily = LEXENDREGULAR,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = Color.White,
                            modifier = Modifier.rotate(rotationState)
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = onExpandDropdown,
                        modifier = Modifier
                            .background(Color(0xFF303030))
                            .fillMaxWidth(0.8f)
                            .heightIn(max = 200.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            availableDrivers.forEach { driver ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = driver.name,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = LEXENDREGULAR,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    onClick = {
                                        onDriverSelected(driver.name)
                                        onExpandDropdown()
                                    },
                                    modifier = Modifier.height(40.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
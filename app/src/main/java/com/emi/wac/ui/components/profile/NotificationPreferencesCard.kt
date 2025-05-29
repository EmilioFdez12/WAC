package com.emi.wac.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.common.Constants.LEXENDBLACK
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.ui.screens.app.CategoryPreference
import com.emi.wac.ui.theme.PrimaryBlue
import com.emi.wac.ui.theme.getPrimaryColorForCategory

/**
 * Composable function to display a card with a list of category preferences
 * Each preference can be toggled to enable or disable notifications
 * You can select a favourite driver for each category
 */
@Composable
fun NotificationPreferencesCard(
    categoryPreferences: List<CategoryPreference>,
    driversMap: Map<String, List<Driver>>,
    expandedDropdownCategory: String?,
    onExpandDropdown: (String?) -> Unit,
    onCategoryToggle: (Int, Boolean) -> Unit,
    onDriverSelect: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF404040),
                            Color(0xFF151515),
                            Color(0xFF151515)
                        )
                    )
                )
                .clip(RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Notifications preferences",
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = LEXENDBLACK,
                modifier = Modifier
                    .background(PrimaryBlue, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category list
            categoryPreferences.forEachIndexed { index, pref ->
                val categoryColor = getPrimaryColorForCategory(pref.name)

                CategoryPreferenceItem(
                    categoryName = pref.name,
                    isEnabled = pref.enabled,
                    favoriteDriver = pref.favoriteDriver,
                    categoryColor = categoryColor,
                    isDropdownExpanded = expandedDropdownCategory == pref.name,
                    availableDrivers = driversMap[pref.name] ?: emptyList(),
                    onToggleEnabled = { isEnabled -> onCategoryToggle(index, isEnabled) },
                    onExpandDropdown = { onExpandDropdown(if (expandedDropdownCategory == pref.name) null else pref.name) },
                    onDriverSelected = { driverName -> onDriverSelect(index, driverName) }
                )

                if (index < categoryPreferences.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
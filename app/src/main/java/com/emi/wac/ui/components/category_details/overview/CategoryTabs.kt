package com.emi.wac.ui.components.category_details.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.getPrimaryColorForCategory

/**
 * Composable function to display a row of category tabs.
 *
 * @param selectedTab The index of the currently selected tab.
 * @param onTabSelected A callback to be invoked when a tab is selected.
 * @param modifier The modifier to be applied to the composable.
 * @param category The racing category (e.g., "F1", "Indycar") to determine styling.
 */
@Composable
fun CategoryTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    category: String,
) {
    val primaryColor = getPrimaryColorForCategory(category)

    Row(modifier = modifier) {
        listOf("OVERVIEW", "STANDINGS", "SCHEDULE").forEachIndexed { index, title ->
            Button(
                onClick = { onTabSelected(index) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == index) primaryColor else PrimaryBlack,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(vertical = 16.dp, horizontal = 0.dp)
            ) {
                Text(
                    text = title,
                    style = AlataTypography.bodyLarge
                )
            }
            if (index < 2) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
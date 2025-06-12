package com.emi.wac.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.PrimaryRed

// Data class to represent a navigation item
private data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

/**
 * Composable function to display a bottom navigation bar
 */
@Composable
fun BottomBar(
    selectedItem: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val bottomBarHeight = when {
        screenWidth < 360.dp -> 76.dp
        screenWidth < 400.dp -> 96.dp
        screenWidth < 600.dp -> 110.dp
        else -> 126.dp
    }

    val items = listOf(
        NavigationItem("Home", Icons.Filled.Home),
        NavigationItem("News", Icons.Filled.Notifications),
        NavigationItem("Profile", Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.padding(bottom = 0.dp)
            .height(bottomBarHeight),
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = {
                    // If the item is already selected, do nothing
                    if (index != selectedItem) {
                        onItemSelected(index)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = PrimaryBlack,
                    unselectedTextColor = PrimaryBlack,
                    selectedIconColor = PrimaryRed,
                    selectedTextColor = PrimaryRed,
                    indicatorColor = Color.Transparent
                ),
                alwaysShowLabel = true
            )
        }
    }
}
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.PrimaryRed

private data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomBar(
    selectedItem: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    val items = listOf(
        NavigationItem("Home", Icons.Filled.Home),
        NavigationItem("News", Icons.Filled.Notifications),
        NavigationItem("Profile", Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.padding(bottom = 0.dp)
            .height(110.dp),
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
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
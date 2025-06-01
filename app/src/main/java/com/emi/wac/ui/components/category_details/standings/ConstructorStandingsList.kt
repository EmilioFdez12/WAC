package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.ui.theme.getPrimaryColorForCategory

/**
 * Composable function to display a list of constructor standings items with adaptive sizing, centered horizontally.
 */
@Composable
fun ConstructorStandingsList(
    standings: List<Constructor>,
    category: String
) {
    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val cardHeight = when {
        screenWidth < 360.dp -> 320.dp
        screenWidth < 400.dp -> 364.dp
        screenWidth < 600.dp -> 570.dp
        else -> 620.dp
    }

    val listPadding = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 400.dp -> 12.dp
        screenWidth < 600.dp -> 16.dp
        else -> 20.dp
    }

    val verticalPadding = when {
        screenWidth < 360.dp -> 4.dp
        screenWidth < 400.dp -> 6.dp
        screenWidth < 600.dp -> 8.dp
        else -> 10.dp
    }

    val dividerThickness = when {
        screenWidth < 360.dp -> 0.3.dp
        screenWidth < 400.dp -> 0.4.dp
        screenWidth < 600.dp -> 0.5.dp
        else -> 0.6.dp
    }

    val horizontalPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 400.dp -> 10.dp
        screenWidth < 600.dp -> 8.dp
        else -> 4.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        ) {
            Box(
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
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                        .padding(listPadding),
                ) {
                    items(standings) { standing ->
                        ConstructorStandingItem(
                            standing = standing,
                            category = category
                        )
                        if (standings.indexOf(standing) < standings.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = verticalPadding),
                                thickness = dividerThickness,
                                color = getPrimaryColorForCategory(category).copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}
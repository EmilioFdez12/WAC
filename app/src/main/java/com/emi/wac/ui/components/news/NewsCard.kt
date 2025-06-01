package com.emi.wac.ui.components.news

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.data.model.news.Article
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.utils.DateUtils

/**
 * Composable function to display a news card
 * Displays the article title, description, author, and date of publication
 * You can click it to open the article in a web browser
 *
 * @param article The article to display
 * @param onCardClick Callback to be invoked when the card is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun NewsCard(
    article: Article,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Log rendering for debugging
    Log.d(
        "NewsCard",
        "Rendering NewsCard for article: ${article.title}, URL: ${article.urlToImage}"
    )

    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive card height
    val titleFontSize = when {
        screenWidth < 360.dp -> 16.sp
        screenWidth < 400.dp -> 18.sp
        screenWidth < 600.dp -> 20.sp
        else -> 22.sp
    }

    // Card container for the news article
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick(article.url) },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        // Main content with gradient background
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
                .clip(RoundedCornerShape(8.dp))
        ) {
            Column {
                // Article image section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (article.urlToImage != null && article.urlToImage.isNotEmpty()) {
                        // Log the image URL being loaded
                        Log.d("NewsCard", "Attempting to load image: ${article.urlToImage}")

                        // Load image with AsyncImage
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(article.urlToImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = article.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop,
                            onError = { state ->
                                Log.e(
                                    "NewsCard",
                                    "Error loading image: ${article.urlToImage}, Cause: ${state.result.throwable.message}"
                                )
                                state.result.throwable.printStackTrace()
                            },
                            onSuccess = {
                                Log.d(
                                    "NewsCard",
                                    "Image loaded successfully: ${article.urlToImage}"
                                )
                            }
                        )
                    }
                }

                // Article content section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title
                    Text(
                        text = article.title,
                        style = AlataTypography.titleMedium.copy(fontSize = titleFontSize),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Description
                    article.description?.let {
                        Text(
                            text = it,
                            style = AlataTypography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Author and date
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "${article.author ?: "Autosport"} - ${
                                DateUtils.formatDate(
                                    article.publishedAt
                                )
                            }",
                            style = AlataTypography.bodySmall,
                            color = PrimaryRed
                        )
                    }
                }
            }
        }
    }
}
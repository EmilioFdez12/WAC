package com.emi.wac.ui.components.news

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.R
import com.emi.wac.data.model.news.Article
import com.emi.wac.data.model.news.Source
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.utils.DateUtils

@Composable
fun NewsCard(
    article: Article,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Log rendering for debugging
    Log.d("NewsCard", "Rendering NewsCard for article: ${article.title}, URL: ${article.urlToImage}")

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
                                Log.d("NewsCard", "Image loaded successfully: ${article.urlToImage}")
                            }
                        )
                    } else {
                        // Show placeholder if no image URL
                        Image(
                            painter = painterResource(id = R.drawable.wac_logo),
                            contentDescription = "Image not available",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                        Log.w("NewsCard", "No image URL for article: ${article.title}")
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
                        style = AlataTypography.titleMedium,
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
                            text = "${article.author ?: "Autosport"} - ${DateUtils.formatDate(article.publishedAt)}",
                            style = AlataTypography.bodySmall,
                            color = PrimaryRed
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsCardPreview() {
    val sampleArticle = Article(
        source = Source("1", "polla"),
        author = "John Doe",
        title = "Sample News Title",
        description = "This is a sample description for the news article.",
        url = "https://example.com",
        urlToImage = "https://cdn-5.motorsport.com/images/amp/2wBNA530/s6/93-re2-1566.jpg",
        publishedAt = "2023-10-01T12:00:00Z",
        content = "sample"
    )
    NewsCard(
        article = sampleArticle,
        onCardClick = {},
        modifier = Modifier.padding(16.dp)
    )
}
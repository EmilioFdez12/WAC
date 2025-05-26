package com.emi.wac.ui.components.news

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.news.Article
import com.emi.wac.ui.components.common.BaseCard
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
    Log.d(
        "NewsCard",
        "Rendering NewsCard for article: ${article.title}, URL: ${article.urlToImage}"
    )
    BaseCard(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = { onCardClick(article.url) },
        gradientColors = listOf(
            Color(0xFF404040),
            Color(0xFF151515),
            Color(0xFF151515)
        ),
        cornerRadius = 8.dp,
        padding = PaddingValues(0.dp)
    ) {
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
package com.emi.wac.ui.screens.app

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emi.wac.ui.components.news.NewsCard
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.viewmodel.NewsViewModel

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = viewModel(),

) {
    val newsState by viewModel.newsState.collectAsState()
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = newsState) {
            is NewsViewModel.NewsState.Loading -> {
                CircularProgressIndicator(color = PrimaryRed)
            }
            
            is NewsViewModel.NewsState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Latest News",
                        style = AlataTypography.titleLarge,
                        color = PrimaryBlack,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn {
                        items(state.articles) { article ->
                            NewsCard(
                                article = article,
                                onCardClick = { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
            
            is NewsViewModel.NewsState.Error -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error loading news",
                        style = AlataTypography.titleMedium,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = state.message,
                        style = AlataTypography.bodyMedium,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.data.model.news.Article
import com.emi.wac.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NewsRepository()
    private val _newsState = MutableStateFlow<NewsState>(NewsState.Loading)
    val newsState: StateFlow<NewsState> = _newsState

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _newsState.value = NewsState.Loading
            try {
                val result = repository.getNews()
                if (result.isSuccess) {
                    val newsResponse = result.getOrNull()
                    if (newsResponse != null && newsResponse.articles.isNotEmpty()) {
                        // Deduplicate articles by URL
                        val uniqueArticle = newsResponse.articles.distinctBy { it.url }
                        _newsState.value = NewsState.Success(uniqueArticle)
                    } else {
                        _newsState.value = NewsState.Error("No news found")
                    }
                } else {
                    _newsState.value = NewsState.Error("Error loading news: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _newsState.value = NewsState.Error("Error loading news: ${e.message}")
            }
        }
    }

    sealed class NewsState {
        object Loading : NewsState()
        data class Success(val articles: List<Article>) : NewsState()
        data class Error(val message: String) : NewsState()
    }
}
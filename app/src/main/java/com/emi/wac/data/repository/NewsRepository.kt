package com.emi.wac.data.repository

import android.content.Context
import com.emi.wac.data.model.news.NewsResponse
import com.emi.wac.data.network.NewsClient.newsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(private val context: Context) {
    
    suspend fun getNews(): Result<NewsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = newsApiService.getNews()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
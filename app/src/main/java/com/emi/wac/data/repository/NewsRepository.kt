package com.emi.wac.data.repository

import com.emi.wac.data.model.news.NewsResponse
import com.emi.wac.data.network.NewsClient.newsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class responsible for fetching news asynchronously.
 */
class NewsRepository() {

    /**
     * Fetches news asynchronously.
     *
     * @return Result containing the NewsResponse if successful, or an error if failed.
     */
    suspend fun getNews(): Result<NewsResponse> = withContext(Dispatchers.IO) {
        try {
            // Fetch news using the news API service
            val response = newsApiService.getNews()
            // Return the successful result
            Result.success(response)
        } catch (e: Exception) {
            // Return the failure result in case of an exception
            Result.failure(e)
        }
    }
}
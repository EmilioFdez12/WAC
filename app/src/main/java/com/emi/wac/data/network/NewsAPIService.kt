package com.emi.wac.data.network

import com.emi.wac.common.Constants.DOMAIN
import com.emi.wac.common.Constants.LANGUAGE
import com.emi.wac.common.Constants.NEWS_API_KEY
import com.emi.wac.common.Constants.SORT
import com.emi.wac.data.model.news.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for the News API service.
 * Defines endpoints for retrieving news articles based on specified parameters.
 */
interface NewsAPIService {

    /**
     * Retrieves news articles from the News API.
     *
     * @param domains Comma-separated list of news domains to filter articles by.
     * @param sortBy The order in which to sort the articles.
     * @param language The language of the articles to retrieve.
     * @param apiKey The API key for accessing the News API.
     *
     * @return A [NewsResponse] containing the retrieved news articles.
     */
    @GET("v2/everything")
    suspend fun getNews(
        @Query("domains") domains: String = DOMAIN,
        @Query("sortBy") sortBy: String = SORT,
        @Query("language") language: String = LANGUAGE,
        @Query("apiKey") apiKey: String = NEWS_API_KEY
    ): NewsResponse
}
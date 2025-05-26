package com.emi.wac.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides a configured Retrofit client for the News API.
 *
 * This client is responsible for creating and configuring the Retrofit instance
 * that will be used throughout the application to make API calls to the news service.
 */
object NewsClient {
    private const val BASE_URL = "https://newsapi.org/"
    // Initialization of the NewsAPIService instance
    val newsApiService: NewsAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsAPIService::class.java)
    }
}
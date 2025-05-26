package com.emi.wac.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides a configured Retrofit client for the Open-Meteo weather API.
 * 
 * This client is responsible for creating and configuring the Retrofit instance
 * that will be used throughout the application to make API calls to the weather service.
 * It uses Gson for JSON parsing
 */
object WeatherClient {
    private const val BASE_URL = "https://api.open-meteo.com/"
    // Initialization of the NewsAPIService instance
    val weatherApiService: WeatherAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherAPIService::class.java)
    }
}
package com.emi.wac.data.network

import com.emi.wac.data.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for the Open-Meteo Weather API service.
 * This interface defines the endpoints for retrieving weather forecast data
 * for specific locations and time periods.
 * 
 * The API provides hourly weather data including temperature and weather conditions
 * which is used to display weather information for race weekends.
 */
interface WeatherAPIService {

    /**
     * Retrieves hourly weather forecast data for a specific location and time period.
     *
     * @param latitude The latitude coordinate of the location.
     * @param longitude The longitude coordinate of the location.
     * @param hourly The specific hourly data to retrieve.
     * @param startDate The start date for the time period.
     * @param endDate The end date for the time period.
     *
     * @return A [WeatherResponse] containing the retrieved weather forecast data.
     */
    @GET("v1/forecast")
    suspend fun getHourlyWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,weathercode",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): WeatherResponse
}
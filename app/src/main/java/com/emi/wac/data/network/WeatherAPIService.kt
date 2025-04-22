package com.emi.wac.data.network

import com.emi.wac.data.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPIService {
    @GET("v1/forecast")
    suspend fun getHourlyWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,weathercode",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): WeatherResponse

}
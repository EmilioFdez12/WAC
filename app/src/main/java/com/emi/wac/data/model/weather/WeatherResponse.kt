package com.emi.wac.data.model.weather

data class WeatherResponse(
    val hourly: Hourly?
)

data class Hourly(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val weathercode: List<Int>,
)

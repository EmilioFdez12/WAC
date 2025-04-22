package com.emi.wac.data.model.weather

data class WeatherData(
    val qualifying: Pair<Float, Int>?,
    val race: Pair<Float, Int>?,
    val sprint: Pair<Float, Int>?
)

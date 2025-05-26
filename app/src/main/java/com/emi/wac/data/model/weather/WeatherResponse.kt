package com.emi.wac.data.model.weather

/**
 * Represents the weather response containing hourly weather data.
 *
 * @property hourly Contains hourly weather information.
 */
data class WeatherResponse(
    val hourly: Hourly?
)

/**
 * Represents hourly weather data.
 *
 * @property time List of time points for the weather data.
 * @property temperature_2m List of temperatures at 2 meters above ground level.
 * @property weathercode List of weather codes representing different weather conditions.
 */
data class Hourly(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val weathercode: List<Int>,
)

/**
 * Represents weather data for specific sessions.
 *
 * @property qualifying Weather data for the qualifying session.
 * @property race Weather data for the race session.
 * @property sprint Weather data for the sprint session.
 */
data class WeatherData(
    val qualifying: Pair<Float, Int>?,
    val race: Pair<Float, Int>?,
    val sprint: Pair<Float, Int>?
)
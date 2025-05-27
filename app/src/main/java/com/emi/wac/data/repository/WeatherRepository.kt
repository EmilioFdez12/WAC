package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.network.WeatherClient
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.model.sessions.Session
import com.emi.wac.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Repository for fetching weather data for racing sessions.
 * It interacts with the RacingRepository to get race details and then fetches
 * weather information from a weather API.
 *
 * @param racingRepository The repository for retrieving racing schedule and circuit information.
 */
class WeatherRepository(private val racingRepository: RacingRepository) {

    private val tag = "WeatherRepository"

    /**
     * Fetches weather data (temperature and weather code) for a specific session of a given category.
     *
     * @param category The racing category (e.g., "f1", "motogp").
     * @param sessionType The type of session (e.g., "qualifying", "sprint", "race").
     * @return A Pair of Float (temperature) and Int (weather code), or null if data cannot be fetched.
     */
    suspend fun getWeatherForSession(
        category: String,
        sessionType: String
    ): Pair<Float, Int>? = withContext(Dispatchers.IO) {
        try {
            Log.d(tag, "Attempting to get weather for $category, session: $sessionType")
            val weatherParams = prepareWeatherParameters(category, sessionType)

            if (weatherParams == null) {
                Log.e(
                    tag,
                    "Failed to prepare weather parameters for $sessionType. Check race/circuit data."
                )
                return@withContext null
            }

            val (lat, lon, isoDate, targetTime) = weatherParams
            val response = WeatherClient.weatherApiService.getHourlyWeather(
                latitude = lat,
                longitude = lon,
                startDate = isoDate,
                endDate = isoDate
            )

            val hourlyData = response.hourly
            if (hourlyData == null) {
                Log.e(tag, "Weather API response has no hourly data for $category, $sessionType.")
                return@withContext null
            }

            val times = hourlyData.time
            val index = times.indexOfFirst { it.startsWith(targetTime) }

            if (index != -1 && hourlyData.temperature_2m.size > index && hourlyData.weathercode.size > index) {
                val temp = hourlyData.temperature_2m[index]
                val code = hourlyData.weathercode[index]
                Log.d(tag, "Successfully fetched weather: Temp=$temp, Code=$code for $targetTime")
                Pair(temp, code)
            } else {
                Log.e(
                    tag,
                    "Target time $targetTime not found or invalid index in weather data for $category, $sessionType. Index: $index, Times size: ${times.size}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching weather for $category, $sessionType: ${e.message}", e)
            null
        }
    }

    /**
     * Prepares the necessary parameters (latitude, longitude, date, time) for a weather API request.
     *
     * @param category The racing category.
     * @param sessionType The type of session.
     * @return WeatherParameters object or null if any required data is missing or invalid.
     */
    private suspend fun prepareWeatherParameters(
        category: String,
        sessionType: String
    ): WeatherParameters? {
        return try {
            val nextRace = racingRepository.getNextGrandPrixObject(category)
            if (nextRace == null) {
                Log.w(tag, "No upcoming race found for category: $category")
                return null
            }

            val circuits = racingRepository.getCircuits(category)
            val circuit = circuits?.circuits?.find {
                it.gp.contains(
                    nextRace.gp,
                    ignoreCase = true
                ) || nextRace.gp.contains(it.gp, ignoreCase = true)
            }
            if (circuit == null) {
                Log.w(tag, "No circuit found for GP: ${nextRace.gp} in category: $category")
                return null
            }

            val locParts = circuit.localization.split(",").map { it.trim() }
            if (locParts.size < 2) {
                Log.e(
                    tag,
                    "Invalid localization format for circuit ${circuit.gp}: ${circuit.localization}"
                )
                return null
            }

            val lat = locParts[0].toDoubleOrNull()
            val lon = locParts[1].toDoubleOrNull()
            if (lat == null || lon == null) {
                Log.e(
                    tag,
                    "Invalid latitude or longitude for circuit ${circuit.gp}: ${circuit.localization}"
                )
                return null
            }

            val session = getSessionByType(nextRace, sessionType)
            if (session == null || session.day.isEmpty() || session.time.isEmpty()) {
                Log.w(
                    tag,
                    "Session data incomplete or not found for type: $sessionType in GP: ${nextRace.gp}"
                )
                return null
            }

            val currentYear = DateUtils.getCurrentYear()
            val dateTime = DateUtils.parseDate(session.day, session.time, currentYear)
            if (dateTime == null) {
                Log.e(tag, "Failed to parse date/time for session: ${session.day} ${session.time}")
                return null
            }

            val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault())

            WeatherParameters(
                latitude = lat,
                longitude = lon,
                date = isoFormat.format(dateTime.time),
                time = targetFormat.format(dateTime.time)
            )
        } catch (e: Exception) {
            Log.e(tag, "Error in prepareWeatherParameters: ${e.message}", e)
            null
        }
    }

    // Helper function to get a specific session from a GrandPrix object based on session type
    private fun getSessionByType(grandPrix: GrandPrix, sessionType: String): Session? =
        when (sessionType) {
            "practice1" -> grandPrix.sessions.practice1
            "practice2" -> grandPrix.sessions.practice2
            "practice3" -> grandPrix.sessions.practice3
            "qualifying" -> grandPrix.sessions.qualifying
            "sprint" -> grandPrix.sessions.sprint
            "sprintQualifying" -> grandPrix.sessions.sprintQualifying
            "race" -> grandPrix.sessions.race
            else -> null
        }

    // Data class to hold weather parameters for API request
    private data class WeatherParameters(
        val latitude: Double,
        val longitude: Double,
        val date: String,
        val time: String
    )
}
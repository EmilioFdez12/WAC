package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.network.WeatherClient
import com.emi.wac.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherRepository(private val racingRepository: RacingRepository) {

    suspend fun getWeatherForSession(
        category: String,
        sessionType: String
    ): Pair<Float, Int>? {
        val tag = "WeatherRepository"

        try {
            Log.d(tag, "Getting weather for $category, session: $sessionType")
            val weatherParams = prepareWeatherParameters(category, sessionType)

            if (weatherParams == null) {
                Log.e(tag, "Failed to prepare weather parameters for $sessionType")
                return null
            }

            val (lat, lon, isoDate, targetTime) = weatherParams
            Log.d(tag, "Weather API call with: lat=$lat, lon=$lon, date=$isoDate, time=$targetTime")

            return withContext(Dispatchers.IO) {
                try {
                    val response = WeatherClient.weatherApiService.getHourlyWeather(
                        latitude = lat,
                        longitude = lon,
                        startDate = isoDate,
                        endDate = isoDate
                    )
                    Log.d(tag, "Raw API response: $response")

                    if (response.hourly == null) {
                        Log.e(tag, "Weather API response has no hourly data")
                        return@withContext null
                    }

                    val times = response.hourly.time
                    Log.d(tag, "Available times in response: ${times.joinToString()}")

                    val index = times.indexOfFirst { it.startsWith(targetTime) }
                    Log.d(tag, "Looking for time $targetTime, found at index: $index")

                    if (index != -1 && response.hourly.temperature_2m.size > index &&
                        response.hourly.weathercode.size > index
                    ) {
                        val temp = response.hourly.temperature_2m[index]
                        val code = response.hourly.weathercode[index]
                        Log.d(tag, "Weather data found: temp=$temp, code=$code")
                        Pair(temp, code)
                    } else {
                        Log.e(
                            tag,
                            "Time not found in response or invalid index: $targetTime, index=$index"
                        )
                        null
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error fetching weather", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error preparing weather parameters", e)
            return null
        }
    }

    private suspend fun prepareWeatherParameters(
        category: String,
        sessionType: String
    ): WeatherParameters? {
        try {
            val nextRace = racingRepository.getNextGrandPrixObject(category) ?: return null
            val circuits = racingRepository.getCircuits(category)
            val circuit = circuits?.circuits?.find {
                it.gp.contains(nextRace.gp, ignoreCase = true) ||
                    nextRace.gp.contains(it.gp, ignoreCase = true)
            } ?: return null

            if (circuit.localization.isEmpty() || !circuit.localization.contains(",")) {
                return null
            }

            val locParts = circuit.localization.split(",")
            if (locParts.size < 2) return null

            val lat = locParts[0].trim().toDoubleOrNull()
            val lon = locParts[1].trim().toDoubleOrNull()
            if (lat == null || lon == null) return null

            val session = when (sessionType) {
                "qualifying" -> nextRace.sessions.qualifying
                "sprint" -> nextRace.sessions.sprint
                else -> nextRace.sessions.race
            } ?: return null

            if (session.day.isEmpty() || session.time.isEmpty()) return null

            val currentYear = DateUtils.getCurrentYear()
            val dateTime =
                DateUtils.parseDate(session.day, session.time, currentYear) ?: return null

            val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault())

            val isoDate = isoFormat.format(dateTime.time)
            val targetTime = targetFormat.format(dateTime.time)

            return WeatherParameters(lat, lon, isoDate, targetTime)
        } catch (_: Exception) {
            return null
        }
    }

    private data class WeatherParameters(
        val latitude: Double,
        val longitude: Double,
        val date: String,
        val time: String
    )
}
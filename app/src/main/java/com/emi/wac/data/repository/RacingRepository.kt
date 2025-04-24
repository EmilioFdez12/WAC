package com.emi.wac.data.repository

import android.content.Context
import android.util.Log
import com.emi.wac.common.Constants.LOADING_RACE_INFO
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.model.circuit.Circuits
import com.emi.wac.data.model.contructor.Constructors
import com.emi.wac.data.model.drivers.Drivers
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.model.sessions.Schedule
import com.emi.wac.data.network.WeatherClient
import com.emi.wac.utils.DateUtils
import com.emi.wac.utils.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Repository class responsible for accessing and managing racing data.
 * This class handles data retrieval from local JSON files and remote weather API.
 *
 * @param context The application context used to access resources
 */
class RacingRepository(context: Context) {
    private val tag = "RacingRepository"
    private val jsonParser = JsonParser(context)

    private val schedule = Schedule::class.java
    private val circuits = Circuits::class.java
    private val drivers = Drivers::class.java
    private val constructors = Constructors::class.java

    /**
     * Retrieves the race schedule for a specific racing category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The Schedule object containing all races for the category, or null if an error occurs
     */
    fun getSchedule(category: String): Schedule? {
        return jsonParser.parseJson("$category/schedule.json", schedule)
            ?: run {
                Log.e(tag, "Error loading schedule")
                null
            }
    }

    /**
     * Retrieves circuit information for a specific racing category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The Circuits object containing all circuit data, or null if an error occurs
     */
    fun getCircuits(category: String): Circuits? {
        return jsonParser.parseJson("$category/circuits.json", circuits)
            ?: run {
                Log.e(tag, "Error loading circuits")
                null
            }
    }

    /**
     * Retrieves driver information for a specific racing category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The Drivers object containing all driver data, or null if an error occurs
     */
    fun getDrivers(category: String): Drivers? {
        return jsonParser.parseJson("$category/drivers.json", drivers)
            ?: run {
                Log.e(tag, "Error loading drivers")
                null
            }
    }

    /**
     * Retrieves constructor/team information for a specific racing category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The Constructors object containing all constructor data, or null if an error occurs
     */
    fun getConstructors(category: String): Constructors? {
        return jsonParser.parseJson("$category/constructors.json", constructors)
            ?: run {
                Log.e(tag, "Error loading constructors")
                null
            }
    }

    /**
     * Gets the next upcoming Grand Prix object for a specific category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The next GrandPrix object, or null if none are found or an error occurs
     */
    fun getNextGrandPrixObject(category: String): GrandPrix? {
        val categorySchedule = getSchedule(category) ?: return null
        val currentDate = Calendar.getInstance()
        val currentYear = DateUtils.getCurrentYear()

        return categorySchedule.schedule.find { grandPrix ->
            DateUtils.isDateAfter(
                currentDate,
                grandPrix.sessions.race.day,
                grandPrix.sessions.race.time,
                currentYear
            )
        }
    }

    /**
     * Creates a RaceInfo object for the next Grand Prix in a category.
     * This includes information about the race and the current championship leader.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @param leaderName The name of the current championship leader
     * @return A RaceInfo object with details about the next race
     */
    fun getNextGrandPrix(category: String, leaderName: String = ""): RaceInfo {
        val currentDate = Calendar.getInstance()
        val currentYear = DateUtils.getCurrentYear()
        val nextRace = getNextGrandPrixObject(category)

        return nextRace?.let { race ->
            val raceDateTime = DateUtils.parseDate(
                race.sessions.race.day,
                race.sessions.race.time,
                currentYear
            ) ?: return LOADING_RACE_INFO

            RaceInfo(
                gpName = race.gp,
                flagPath = race.flag,
                timeRemaining = DateUtils.calculateTimeRemaining(raceDateTime, currentDate),
                leaderImagePath = getDriverPortrait(category, leaderName),
                leaderName = leaderName
            )
        } ?: LOADING_RACE_INFO
    }

    /**
     * Retrieves the portrait image path for a specific driver.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @param driverName The name of the driver
     * @return The path to the driver's portrait image, or an empty string if not found
     */
    private fun getDriverPortrait(category: String, driverName: String): String {
        try {
            if (driverName.isEmpty()) return ""
            val drivers = getDrivers(category)
            val driverPortrait = drivers?.drivers?.find { it.name == driverName }?.portrait ?: ""
            return driverPortrait
        } catch (e: Exception) {
            Log.e(tag, "Error finding driver portrait", e)
            return ""
        }
    }

    /**
     * Obtains the weather forecast for a specific session of the next race.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @param sessionType The type of session (qualifying, race, sprint)
     * @return A pair with the temperature and weather code for the session, or null if an error occurred
     */
    suspend fun getWeatherForSession(
        category: String,
        sessionType: String
    ): Pair<Float, Int>? {
        val weatherParams = prepareWeatherParameters(category, sessionType) ?: return null
        val (lat, lon, isoDate, targetTime) = weatherParams

        return withContext(Dispatchers.IO) {
            try {
                val response = WeatherClient.weatherApiService.getHourlyWeather(
                    latitude = lat,
                    longitude = lon,
                    startDate = isoDate,
                    endDate = isoDate
                )
                Log.d(tag, "Weather response: $response")
                val index = response.hourly?.time?.indexOf(targetTime) ?: -1
                if (index != -1) {
                    Pair(
                        response.hourly!!.temperature_2m[index],
                        response.hourly.weathercode[index]
                    )
                } else {
                    Log.e(tag, "Time not found in response or hourly is null: $targetTime")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching weather", e)
                null
            }
        }
    }

    /**
     * Prepares the parameters needed for a weather API query.
     * This method extracts location coordinates and formats date/time for the API request.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @param sessionType The type of session (qualifying, race, sprint)
     * @return An object with the parameters needed for the weather API, or null if an error occurs
     */
    private fun prepareWeatherParameters(
        category: String,
        sessionType: String
    ): WeatherParameters? {
        // Obtain next gp
        val nextRace = getNextGrandPrixObject(category) ?: return null
        val circuits = getCircuits(category) ?: return null
        // Obtain next circuit
        val circuit = circuits.circuits.find { it.gp == nextRace.gp } ?: return null

        val (lat, lon) = circuit.localization.split(",").map { it.trim().toDouble() }
        val session =
            if (sessionType == "qualifying") nextRace.sessions.qualifying else if (sessionType == "sprint") nextRace.sessions.sprint else nextRace.sessions.race
        // Calculate date for API query
        val currentYear = DateUtils.getCurrentYear()
        val dateTime =
            DateUtils.parseDate(session?.day ?: "", session?.time ?: "", currentYear) ?: return null
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault())

        val isoDate = isoFormat.format(dateTime.time)
        val targetTime = targetFormat.format(dateTime.time)

        // Return parameters
        return WeatherParameters(lat, lon, isoDate, targetTime)
    }

    /**
     * Data class to store parameters needed for weather API queries.
     */
    private data class WeatherParameters(
        val latitude: Double,
        val longitude: Double,
        val date: String,
        val time: String
    )
}
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

class RacingRepository(private val context: Context) {
    private val tag = "RacingRepository"
    private val jsonParser = JsonParser(context)

    private val schedule = Schedule::class.java
    private val circuits = Circuits::class.java
    private val drivers = Drivers::class.java
    private val constructors = Constructors::class.java

    fun getSchedule(category: String): Schedule? {
        return jsonParser.parseJson("$category/schedule.json", schedule)
            ?: run {
                Log.e(tag, "Error loading schedule")
                null
            }
    }

    fun getCircuits(category: String): Circuits? {
        return jsonParser.parseJson("$category/circuits.json", circuits)
            ?: run {
                Log.e(tag, "Error loading circuits")
                null
            }
    }

    fun getDrivers(category: String): Drivers? {
        return jsonParser.parseJson("$category/drivers.json", drivers)
            ?: run {
                Log.e(tag, "Error loading drivers")
                null
            }
    }

    fun getConstructors(category: String): Constructors? {
        return jsonParser.parseJson("$category/constructors.json", constructors)
            ?: run {
                Log.e(tag, "Error loading constructors")
                null
            }
    }

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

    suspend fun getWeatherForSession(
        category: String,
        sessionType: String
    ): Pair<Float, Int>? {
        val nextRace = getNextGrandPrixObject(category) ?: return null
        val circuits = getCircuits(category) ?: return null
        val circuit = circuits.circuits.find { it.gp == nextRace.gp } ?: return null

        val (lat, lon) = circuit.localization.split(",").map { it.trim().toDouble() }
        val session = if (sessionType == "qualifying") nextRace.sessions.qualifying else nextRace.sessions.race
        val currentYear = DateUtils.getCurrentYear()

        val dateTime = DateUtils.parseDate(session?.day ?: "", session?.time ?: "", currentYear) ?: return null
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val isoDate = isoFormat.format(dateTime.time)
        val targetFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault())
        val targetTime = targetFormat.format(dateTime.time)

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
                    Log.e(tag, "Hora no encontrada en la respuesta o hourly es null: $targetTime")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching weather", e)
                null
            }
        }
    }
}
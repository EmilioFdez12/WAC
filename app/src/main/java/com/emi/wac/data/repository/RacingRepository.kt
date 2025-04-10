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
import com.emi.wac.data.utils.DateUtils
import com.emi.wac.data.utils.JsonParser
import java.util.Calendar

/**
 * Repository class responsible for fetching and processing racing-related data.
 *
 * @property context The application context used to access assets and resources
 */
class RacingRepository(private val context: Context) {
    private val tag = "RacingRepository"
    private val jsonParser = JsonParser(context)

    // Class objects used for parsing JSON
    private val schedule = Schedule::class.java
    private val circuits = Circuits::class.java
    private val drivers = Drivers::class.java
    private val constructors = Constructors::class.java

    /**
     * Retrieves the racing schedule for a specific category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return Schedule object containing all races, null if fails
     */
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
                Log.e(tag, "Error loading drivers")
                null
            }
    }

    /**
     * Gets the next upcoming Grand Prix for a category.
     *
     * @param category The racing category to check
     * @return The next GrandPrix object or null if none found
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
     * Gets information about the next upcoming Grand Prix for a category.
     *
     * @param category The racing category to check
     * @param leaderName Name of the current championship leader
     * @return RaceInfo object containing details about the next race
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
     * Retrieves the portrait of a driver for a specific category.
     */
    private fun getDriverPortrait(category: String, driverName: String): String {
        try {
            if (driverName.isEmpty()) return ""
            val drivers = getDrivers(category)
            // Find the driver in the list who matches the name
            val driverPortrait = drivers?.drivers?.find { it.name == driverName }?.portrait ?: ""
            return driverPortrait
        } catch (e: Exception) {
            Log.e(tag, "Error finding driver portrait", e)
            return ""
        }
    }
}
package com.emi.wac.data.repository

import android.content.Context
import android.util.Log
import com.emi.wac.common.Constants
import com.emi.wac.data.model.sessions.Schedule
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.model.drivers.Drivers
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository class responsible for fetching and processing racing-related data.
 *
 * @property context The application context used to access assets and resources
 */
class RacingRepository(private val context: Context) {
    private val tag = "RacingRepository"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // Date format used for parsing race dates from JSON
    private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH)

    /**
     * Retrieves the racing schedule for a specific category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return Schedule object containing all races, null if fails
     */
    fun getSchedule(category: String): Schedule? {
        return try {
            // Read schedule JSON file from assets
            val jsonString = context.assets
                .open("$category/schedule.json")
                .bufferedReader()
                .use { it.readText() }

            // Parse JSON into Schedule object using Moshi
            moshi.adapter(Schedule::class.java).fromJson(jsonString)
        } catch (e: Exception) {
            Log.e(tag, "Error loading schedule", e)
            null
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
        // Get schedule or return default "no races" info if loading fails
        val categorySchedule = getSchedule(category) ?: return createNoRaceInfo(category)
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)

        // Find the next race of the schedule
        val nextRace = categorySchedule.schedule.find { grandPrix ->
            val raceDay =
                "${grandPrix.sessions.race.day} $currentYear ${grandPrix.sessions.race.time}"
            // Checks if race date is after current date
            dateFormat.parse(raceDay)?.after(currentDate.time) == true
        }

        return nextRace?.let { race ->
            // Parse race date/time or return default if parsing fails
            val raceDateTime =
                dateFormat.parse("${race.sessions.race.day} $currentYear ${race.sessions.race.time}")
                    ?: return createNoRaceInfo(category)

            RaceInfo(
                gpName = race.gp,
                flagPath = race.flag,
                timeRemaining = calculateTimeRemaining(raceDateTime, currentDate),
                leaderImagePath = getDriverPortrait(category, leaderName),
                leaderName = leaderName
            )
        } ?: createNoRaceInfo(category)
    }

    /**
     * Retrieves the portrait image path for a specific driver.
     *
     * @param category The racing category the driver belongs to
     * @param driverName The name of the driver to find
     * @return Path to the driver's portrait image, or empty string if not found
     */
    private fun getDriverPortrait(category: String, driverName: String): String {
        if (driverName.isEmpty()) return ""

        return try {
            // Read drivers JSON file from assets
            val jsonString = context.assets
                .open("$category/drivers.json")
                .bufferedReader()
                .use { it.readText() }

            // Find driver by name and return portrait path
            moshi.adapter(Drivers::class.java)
                .fromJson(jsonString)
                ?.pilotos
                ?.find { it.name == driverName }
                ?.portrait
                ?: ""
        } catch (e: Exception) {
            Log.e(tag, "Error finding driver portrait", e)
            ""
        }
    }

    /**
     * Calculates the remaining time until a race starts.
     *
     * @param raceDate The date/time of the race
     * @param currentDate Current date/time for comparison
     * @return Formatted string showing days, hours and minutes remaining
     */
    private fun calculateTimeRemaining(raceDate: Date, currentDate: Calendar): String {
        // Calculate time difference in milliseconds
        val diff = raceDate.time - currentDate.timeInMillis

        val days = diff / Constants.MILLISECONDS_PER_DAY
        val hours = (diff % Constants.MILLISECONDS_PER_DAY) / Constants.MILLISECONDS_PER_HOUR
        val minutes = (diff % Constants.MILLISECONDS_PER_HOUR) / Constants.MILLISECONDS_PER_MINUTE
        val seconds = (diff % Constants.MILLISECONDS_PER_MINUTE) / 1000

        return if (days > 0) {
            "${days}d ${hours}h ${minutes}m"
        } else {
            "${hours}h ${minutes}m ${seconds}s"
        }
    }

    /**
     * Creates a default RaceInfo object when no upcoming races are found.
     *
     * @param category The racing category that was checked
     * @return RaceInfo with default "no races" values
     */
    private fun createNoRaceInfo(category: String): RaceInfo {
        return RaceInfo(
            gpName = "No upcoming $category races",
            flagPath = "",
            timeRemaining = "",
            leaderImagePath = "",
            leaderName = ""
        )
    }
}
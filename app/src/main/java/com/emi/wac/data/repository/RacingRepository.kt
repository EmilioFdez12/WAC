package com.emi.wac.data.repository

import android.content.Context
import android.util.Log
import com.emi.wac.common.Constants.LOADING_RACE_INFO
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.model.circuit.Circuits
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.model.sessions.Schedule
import com.emi.wac.data.model.sessions.Sessions
import com.emi.wac.utils.DateUtils
import com.emi.wac.utils.JsonParser
import com.emi.wac.utils.SessionsUtils.createSession
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Repository class responsible for managing racing data.
 * Provides methods to fetch schedules, circuits, and upcoming race information.
 * Firebase for data retrieval and parsing utilities for data processing.
 *
 * @param standingsRepository The repository for retrieving driver standings.
 * @param context The application context.
 */
class RacingRepository(
    private val standingsRepository: StandingsRepository,
    private val context: Context
) {
    private val tag = "RacingRepository"
    private val jsonParser = JsonParser(context)
    private val circuitsClass = Circuits::class.java

    /**
     * Safely casts an Any? type to a specified reified type T.
     * @return The casted object of type T, or null if the cast fails.
     */
    private inline fun <reified T> Any?.safeCastTo(): T? = this as? T

    /**
     * Converts a Map<*, *> to a Map<String, Any> if all keys are Strings and values are non-null.
     * @return A Map<String, Any> or null if conversion is not possible.
     */
    private fun Map<*, *>.toStringAnyMap(): Map<String, Any>? =
        this.entries.associate { (key, value) ->
            (key as? String ?: return null) to (value ?: return null)
        }

    /**
     * Fetches the schedule for a given category from Firebase.
     *
     * @param category The category of the race (e.g., F1, MotoGP).
     * @return A Schedule object containing the list of Grand Prix, or null if an error occurs.
     */
    suspend fun getSchedule(category: String): Schedule? = try {
        val collectionRef = Firebase.firestore.collection("${category}_schedule")
        val documents = collectionRef.get().await()

        val scheduleList = documents.mapNotNull { scheduleDocument ->
            try {
                val data = scheduleDocument.data
                val gp = data["gp"] as? String
                val dates = data["dates"] as? String
                val flag = data["flag"] as? String
                val sessionsMap = data["sessions"] as? Map<*, *>

                if (gp == null || sessionsMap == null) {
                    Log.w(
                        tag,
                        "Skipping document ${scheduleDocument.id}: Missing 'gp' or 'sessions' data."
                    )
                    return@mapNotNull null
                }

                val sessions = createSessionsFromMap(sessionsMap)
                if (sessions == null) {
                    Log.w(
                        tag,
                        "Skipping document ${scheduleDocument.id}: Could not create sessions from map."
                    )
                    return@mapNotNull null
                }

                GrandPrix(
                    gp = gp,
                    dates = dates ?: "",
                    flag = flag ?: "",
                    sessions = sessions
                )
            } catch (e: Exception) {
                Log.e(
                    tag,
                    "Error processing scheduleDocument ${scheduleDocument.id}: ${e.message}",
                    e
                )
                null
            }
        }
        if (scheduleList.isNotEmpty()) Schedule(scheduleList) else null
    } catch (e: Exception) {
        Log.e(tag, "Error loading schedule from Firebase: ${e.message}", e)
        null
    }

    /**
     * Retrieves the circuits for a given category.
     *
     * @param category The category of the race.
     * @return A Circuits object or null if an error occurs.
     */
    fun getCircuits(category: String): Circuits? =
        jsonParser.parseJson("$category/circuits.json", circuitsClass) ?: run {
            Log.e(tag, "Error loading circuits for category: $category")
            null
        }

    /**
     * Finds the next Grand Prix object for a given category.
     *
     * @param category The category of the race.
     * @return A GrandPrix object or null if no upcoming race is found.
     */
    suspend fun getNextGrandPrixObject(category: String): GrandPrix? {
        val categorySchedule = getSchedule(category) ?: return null
        val currentDate = Calendar.getInstance()
        val currentYear = DateUtils.getCurrentYear()

        return categorySchedule.schedule.firstOrNull { grandPrix ->
            // Check if ANY session has valid data and is in the future
            listOf(
                grandPrix.sessions.practice1,
                grandPrix.sessions.practice2,
                grandPrix.sessions.practice3,
                grandPrix.sessions.sprintQualifying,
                grandPrix.sessions.sprint,
                grandPrix.sessions.qualifying,
                grandPrix.sessions.race
            ).any { session ->
                session?.let {
                    it.day.isNotEmpty() && it.time.isNotEmpty() &&
                        DateUtils.isDateAfter(
                            currentDate,
                            it.day,
                            it.time,
                            currentYear
                        )
                } == true
            }
        }
    }

    /**
     * Retrieves the next Grand Prix race information for a given category.
     *
     * @param category The category of the race.
     * @param leaderName The name of the current leader.
     * @return A RaceInfo object containing details of the next race.
     */
    suspend fun getNextGrandPrix(category: String, leaderName: String = ""): RaceInfo {
        val currentDate = Calendar.getInstance()
        val currentYear = DateUtils.getCurrentYear()
        val nextRace = getNextGrandPrixObject(category)

        return nextRace?.let { race ->
            // Find any valid session instead of just race session
            val validSession = listOf(
                race.sessions.practice1,
                race.sessions.practice2,
                race.sessions.practice3,
                race.sessions.sprintQualifying,
                race.sessions.sprint,
                race.sessions.qualifying,
                race.sessions.race
            ).firstOrNull { session ->
                session?.let {
                    it.day.isNotEmpty() && it.time.isNotEmpty() &&
                        DateUtils.isDateAfter(
                            currentDate,
                            it.day,
                            it.time,
                            currentYear
                        )
                } == true
            }

            if (validSession == null) {
                Log.w(tag, "No valid future sessions found for GP: ${race.gp}")
                return LOADING_RACE_INFO
            }

            val sessionDateTime = DateUtils.parseDate(
                validSession.day,
                validSession.time,
                currentYear
            ) ?: run {
                Log.e(tag, "Failed to parse date for session: ${race.gp}")
                return LOADING_RACE_INFO
            }

            RaceInfo(
                gpName = race.gp,
                flagPath = race.flag,
                timeRemaining = DateUtils.calculateTimeRemaining(sessionDateTime, currentDate),
                leaderImagePath = getDriverPortrait(category, leaderName),
                leaderName = leaderName
            )
        } ?: LOADING_RACE_INFO
    }

    /**
     * Retrieves the portrait of a driver given the category and driver name.
     *
     * @param category The category of the race.
     * @param driverName The name of the driver.
     * @return The URL of the driver's portrait, or an empty string if not found or an error occurs.
     */
    private suspend fun getDriverPortrait(category: String, driverName: String): String = try {
        if (driverName.isEmpty()) ""
        else standingsRepository.getDriverStandings(category).getOrNull()
            ?.find { it.name == driverName }?.portrait ?: ""
    } catch (e: Exception) {
        Log.e(tag, "Error finding driver portrait for $driverName in $category", e)
        ""
    }

    /**
     * Creates a Sessions object from a map of session data.
     *
     * @param sessionsMap The map containing session data.
     * @return A Sessions object, or null if the race session is missing.
     */
    private fun createSessionsFromMap(sessionsMap: Map<*, *>): Sessions? {
        val practice1 = sessionsMap["practice1"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
            ?.let { createSession(it) }
        val practice2 = sessionsMap["practice2"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
            ?.let { createSession(it) }
        val practice3 = sessionsMap["practice3"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
            ?.let { createSession(it) }
        val qualifying = sessionsMap["qualifying"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
            ?.let { createSession(it) }
        val sprint = sessionsMap["sprint"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
            ?.let { createSession(it) }
        val sprintQualifying =
            sessionsMap["sprintQualifying"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
                ?.let { createSession(it) }
        val race =
            sessionsMap["race"].safeCastTo<Map<*, *>>()?.toStringAnyMap()?.let { createSession(it) }

        val hasValidSession =
            listOf(practice1, practice2, practice3, qualifying, sprint, sprintQualifying, race)
                .any { it != null }

        if (!hasValidSession) {
            Log.w("RacingRepository", "No valid sessions found in sessionsMap: $sessionsMap")
            return null
        }

        return Sessions(
            practice1 = practice1,
            practice2 = practice2,
            practice3 = practice3,
            qualifying = qualifying,
            sprint = sprint,
            sprintQualifying = sprintQualifying,
            race = race
        )
    }
}
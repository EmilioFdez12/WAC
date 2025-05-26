package com.emi.wac.data.repository

import android.content.Context
import android.util.Log
import com.emi.wac.common.Constants.LOADING_RACE_INFO
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.model.circuit.Circuits
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.model.sessions.Schedule
import com.emi.wac.data.model.sessions.Session
import com.emi.wac.data.model.sessions.Sessions
import com.emi.wac.utils.DateUtils
import com.emi.wac.utils.JsonParser
import com.emi.wac.utils.SessionsUtils.createSession
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

/**
 * Repository class responsible for managing racing data.
 * Provides methods to fetch schedules, circuits, and upcoming race information.
 * Firebase for data retrieval and parsing utilities for data processing.
 *
 * @param standingsRepository The repository for retrieving driver standings.
 * @param context The application context.
 */
class RacingRepository(private val standingsRepository: StandingsRepository, context: Context) {
    private val tag = "RacingRepository"
    private val jsonParser = JsonParser(context)
    private val circuits = Circuits::class.java

    private inline fun <reified T> Any?.safeCastTo(): T? {
        return this as? T
    }

    private fun Map<*, *>.toStringAnyMap(): Map<String, Any>? {
        return this.entries.associate { (key, value) ->
            (key as? String ?: return null) to (value ?: return null)
        }
    }

    /**
     * Fetches the schedule for a given category from Firebase.
     *
     * @param category The category of the race (e.g., F1, MotoGP).
     * @return A Schedule object containing the list of Grand Prix, or null if an error occurs.
     */
    suspend fun getSchedule(category: String): Schedule? {
        try {
            val scheduleList = mutableListOf<GrandPrix>()
            val collectionRef = Firebase.firestore.collection("${category}_schedule")
            val documents = collectionRef.get().await()

            for (scheduleDocument in documents) {
                try {
                    val data = scheduleDocument.data
                    val gp = data["gp"] as? String ?: continue
                    val dates = data["dates"] as? String ?: ""
                    val flag = data["flag"] as? String ?: ""

                    val sessionsMap = data["sessions"] as? Map<*, *> ?: continue
                    val sessions = createSessionsFromMap(sessionsMap) ?: continue

                    scheduleList.add(
                        GrandPrix(
                            gp = gp,
                            dates = dates,
                            flag = flag,
                            sessions = sessions
                        )
                    )
                } catch (e: Exception) {
                    Log.e(
                        tag,
                        "Error processing scheduleDocument ${scheduleDocument.id}: ${e.message}",
                        e
                    )
                }
            }
            if (scheduleList.isNotEmpty()) {
                return Schedule(scheduleList)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error loading schedule from Firebase: ${e.message}", e)
        }
        return null
    }


    /**
     * Retrieves the circuits for a given category.
     *
     * @param category The category of the race.
     * @return A Circuits object or null if an error occurs.
     */
    fun getCircuits(category: String): Circuits? {
        return jsonParser.parseJson("$category/circuits.json", circuits) ?: run {
            Log.e(tag, "Error loading circuits")
            null
        }
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

        return categorySchedule.schedule.find { grandPrix ->
            // Check for session race
            val sessions = listOfNotNull(
                grandPrix.sessions.race.takeIf { it?.day?.isNotEmpty() == true && it.time.isNotEmpty() },
            )
            // Check if any is after the current date
            sessions.isNotEmpty() && sessions.any { session ->
                DateUtils.isDateAfter(
                    currentDate,
                    session.day,
                    session.time,
                    currentYear
                )
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
            // Find the first available session race
            val sessions = listOfNotNull(
                race.sessions.race.takeIf { it?.day?.isNotEmpty() == true && it.time.isNotEmpty() },
            )

            // Sort sessions by date and take the first
            val nextSession = sessions.minByOrNull { session ->
                val sessionDate = DateUtils.parseDate(session.day, session.time, currentYear)
                    ?: Date(Long.MAX_VALUE)
                sessionDate.time
            } ?: return LOADING_RACE_INFO

            val sessionDateTime = DateUtils.parseDate(
                nextSession.day,
                nextSession.time,
                currentYear
            ) ?: return LOADING_RACE_INFO
            // Create a RaceInfo object
            RaceInfo(
                gpName = race.gp,
                flagPath = race.flag,
                timeRemaining = DateUtils.calculateTimeRemaining(sessionDateTime, currentDate),
                leaderImagePath = getDriverPortrait(category, leaderName),
                leaderName = leaderName
            )
            // If no race is found, return LOADING_RACE_INFO
        } ?: LOADING_RACE_INFO
    }

    // Retrieves the portrait of a driver given the category and driver name
    private suspend fun getDriverPortrait(category: String, driverName: String): String {
        try {
            if (driverName.isEmpty()) return ""
            val driversResult = standingsRepository.getDriverStandings(category)
            if (driversResult.isSuccess) {
                val drivers = driversResult.getOrNull()
                return drivers?.find { it.name == driverName }?.portrait ?: ""
            }
            return ""
        } catch (e: Exception) {
            Log.e(tag, "Error finding driver portrait", e)
            return ""
        }
    }

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
        val race = sessionsMap["race"].safeCastTo<Map<*, *>>()?.toStringAnyMap()
            ?.let { createSession(it) } ?: return null

        return Sessions(
            practice1 = practice1 ?: Session("", ""),
            practice2 = practice2,
            practice3 = practice3,
            qualifying = qualifying,
            sprint = sprint,
            sprintQualifying = sprintQualifying,
            race = race
        )
    }
}
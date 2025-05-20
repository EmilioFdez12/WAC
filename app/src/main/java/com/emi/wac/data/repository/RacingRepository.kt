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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class RacingRepository(private val standingsRepository: StandingsRepository, context: Context) {
    private val tag = "RacingRepository"
    private val jsonParser = JsonParser(context)

    private val circuits = Circuits::class.java

    suspend fun getSchedule(category: String): Schedule? {
        try {
            val scheduleList = mutableListOf<GrandPrix>()
            val collectionRef = Firebase.firestore.collection("${category}_schedule")
            val documents = collectionRef.get().await()

            for (document in documents) {
                try {
                    val data = document.data
                    val gp = data["gp"] as? String ?: continue
                    val dates = data["dates"] as? String ?: ""
                    val flag = data["flag"] as? String ?: ""

                    val sessionsMap = data["sessions"] as? Map<*, *> ?: continue

                    val practice1 = createSession(sessionsMap["practice1"] as? Map<String, Any>)
                    val practice2 = createSession(sessionsMap["practice2"] as? Map<String, Any>)
                    val practice3 = createSession(sessionsMap["practice3"] as? Map<String, Any>)
                    val qualifying = createSession(sessionsMap["qualifying"] as? Map<String, Any>)
                    val sprint = createSession(sessionsMap["sprint"] as? Map<String, Any>)
                    val sprintQualifying =
                        createSession(sessionsMap["sprintQualifying"] as? Map<String, Any>)
                    val race = createSession(sessionsMap["race"] as? Map<String, Any>) ?: continue

                    val sessions = Sessions(
                        practice1 = practice1 ?: Session("", ""),
                        practice2 = practice2,
                        practice3 = practice3,
                        qualifying = qualifying,
                        sprint = sprint,
                        sprintQualifying = sprintQualifying,
                        race = race
                    )

                    scheduleList.add(
                        GrandPrix(
                            gp = gp,
                            dates = dates,
                            flag = flag,
                            sessions = sessions
                        )
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Error processing document ${document.id}: ${e.message}", e)
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

    private fun createSession(sessionMap: Map<String, Any>?): Session? {
        if (sessionMap == null) return null

        var day = sessionMap["day"] as? String ?: ""
        var time = sessionMap["time"] as? String ?: ""

        // Si day o time están vacíos, intentamos extraerlos del formato ISO
        if (day.isEmpty() || time.isEmpty()) {
            val isoDateTime = sessionMap["isoDateTime"] as? String
            if (isoDateTime != null && isoDateTime.isNotEmpty()) {
                try {
                    val dateTime = isoDateTime.split("T")
                    if (dateTime.size >= 2) {
                        val dateParts = dateTime[0].split("-")
                        if (dateParts.size >= 3) {
                            day = when (dateParts[1]) {
                                "01" -> "${dateParts[2]} JAN"
                                "02" -> "${dateParts[2]} FEB"
                                "03" -> "${dateParts[2]} MAR"
                                "04" -> "${dateParts[2]} APR"
                                "05" -> "${dateParts[2]} MAY"
                                "06" -> "${dateParts[2]} JUN"
                                "07" -> "${dateParts[2]} JUL"
                                "08" -> "${dateParts[2]} AUG"
                                "09" -> "${dateParts[2]} SEP"
                                "10" -> "${dateParts[2]} OCT"
                                "11" -> "${dateParts[2]} NOV"
                                "12" -> "${dateParts[2]} DEC"
                                else -> "${dateParts[2]} ${dateParts[1]}"
                            }
                        }

                        val timePart = dateTime[1].split("+")[0].split(".")[0]
                        if (timePart.isNotEmpty()) {
                            val timeParts = timePart.split(":")
                            if (timeParts.size >= 2) {
                                time = "${timeParts[0]}:${timeParts[1]}"
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error parsing isoDateTime: $isoDateTime", e)
                }
            }
        }

        // Verificamos si después de intentar extraer del formato ISO, aún tenemos valores vacíos
        if (day.isEmpty() || time.isEmpty()) {
            Log.w(tag, "Session skipped: day=$day, time=$time, map=$sessionMap")
            return null
        }

        return Session(day, time)
    }

    fun getCircuits(category: String): Circuits? {
        return jsonParser.parseJson("$category/circuits.json", circuits) ?: run {
            Log.e(tag, "Error loading circuits")
            null
        }
    }

    suspend fun getNextGrandPrixObject(category: String): GrandPrix? {
        val categorySchedule = getSchedule(category) ?: return null
        val currentDate = Calendar.getInstance()
        val currentYear = DateUtils.getCurrentYear()

        return categorySchedule.schedule.find { grandPrix ->
            // Verificar cualquier sesión disponible, no solo la carrera
            val sessions = listOfNotNull(
                grandPrix.sessions.race.takeIf { it?.day?.isNotEmpty() == true && it.time.isNotEmpty() },
                grandPrix.sessions.qualifying?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                grandPrix.sessions.practice3?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                grandPrix.sessions.practice2?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                grandPrix.sessions.practice1.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                grandPrix.sessions.sprint?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                grandPrix.sessions.sprintQualifying?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() }
            )
            
            // Si hay al menos una sesión válida, verificar si alguna es después de la fecha actual
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

    suspend fun getNextGrandPrix(category: String, leaderName: String = ""): RaceInfo {
        val currentDate = Calendar.getInstance()
        val currentYear = DateUtils.getCurrentYear()
        val nextRace = getNextGrandPrixObject(category)

        return nextRace?.let { race ->
            // Buscar la primera sesión disponible (carrera u otra)
            val sessions = listOfNotNull(
                race.sessions.race.takeIf { it?.day?.isNotEmpty() == true && it.time.isNotEmpty() },
                race.sessions.qualifying?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                race.sessions.practice3?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                race.sessions.practice2?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                race.sessions.practice1.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                race.sessions.sprint?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() },
                race.sessions.sprintQualifying?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() }
            )
            
            // Ordenar sesiones por fecha y tomar la primera
            val nextSession = sessions.minByOrNull { session ->
                val sessionDate = DateUtils.parseDate(session.day, session.time, currentYear) ?: Date(Long.MAX_VALUE)
                sessionDate.time
            } ?: return LOADING_RACE_INFO
            
            val sessionDateTime = DateUtils.parseDate(
                nextSession.day,
                nextSession.time,
                currentYear
            ) ?: return LOADING_RACE_INFO

            RaceInfo(
                gpName = race.gp,
                flagPath = race.flag,
                timeRemaining = DateUtils.calculateTimeRemaining(sessionDateTime, currentDate),
                leaderImagePath = getDriverPortrait(category, leaderName),
                leaderName = leaderName
            )
        } ?: LOADING_RACE_INFO
    }

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
}
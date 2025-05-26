package com.emi.wac.utils

import android.util.Log
import com.emi.wac.data.model.sessions.Session

/**
 * Utility functions for handling race sessions.
 */
object SessionsUtils {

    const val TAG = "SessionsUtils"
    const val DEFAULT_MONTH = 1
    const val DEFAULT_NUMERIC_ORDER = 0

    /**
     * Sorts a list of sessions by date and time in descending order.
     *
     * @param sessions List of sessions as Triple<name, day, time>
     * @return Sorted list of sessions
     */
    fun sortSessionsByDateDesc(sessions: List<Triple<String, String, String>>): List<Triple<String, String, String>> {
        return sessions.sortedWith(compareByDescending { (_, day, time) ->
            try {
                val dateParts = day.split(" ")
                val dayNum = dateParts[0].toInt()
                val month = when (dateParts[1]) {
                    "Jan" -> 1
                    "Feb" -> 2
                    "Mar" -> 3
                    "Apr" -> 4
                    "May" -> 5
                    "Jun" -> 6
                    "Jul" -> 7
                    "Aug" -> 8
                    "Sep" -> 9
                    "Oct" -> 10
                    "Nov" -> 11
                    "Dec" -> 12
                    else -> DEFAULT_MONTH
                }

                val timeParts = time.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()

                // Create a numeric value for sorting (month * 1000000 + day * 10000 + hour * 100 + minute)
                month * 1000000 + dayNum * 10000 + hour * 100 + minute
            } catch (_: Exception) {
                DEFAULT_NUMERIC_ORDER
            }
        })
    }

    /**
     * Creates a Session object from a map of session data.
     *
     * @param sessionMap Map containing session data
     * @return Session object or null if data is invalid
     */
    fun createSession(sessionMap: Map<String, Any>?): Session? {
        if (sessionMap == null) return null

        var day = sessionMap["day"] as? String ?: ""
        var time = sessionMap["time"] as? String ?: ""

        // If day or time are empty, try to extract them from the ISO format
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
                    Log.e(TAG, "Error parsing isoDateTime: $isoDateTime", e)
                }
            }
        }

        // Check if after attempting to extract from ISO format, we still have empty values
        if (day.isEmpty() || time.isEmpty()) {
            Log.w(TAG, "Session skipped: day=$day, time=$time, map=$sessionMap")
            return null
        }

        return Session(day, time)
    }
}
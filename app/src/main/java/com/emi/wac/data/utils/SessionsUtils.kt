package com.emi.wac.data.utils

/**
 * Utility functions for handling race sessions.
 */
object SessionsUtils {
    
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
                    else -> 1
                }
                
                val timeParts = time.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                
                // Crear un valor numérico para ordenar (mes * 1000000 + día * 10000 + hora * 100 + minuto)
                month * 1000000 + dayNum * 10000 + hour * 100 + minute
            } catch (e: Exception) {
                0
            }
        })
    }
}
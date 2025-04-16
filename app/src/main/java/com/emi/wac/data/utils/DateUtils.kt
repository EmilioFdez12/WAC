package com.emi.wac.data.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Object containing utility functions for date-related operations.
 */
object DateUtils {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH)


    fun parseDate(day: String, time: String, year: Int): Date? {
        val formattedTime = formatTime(time)
        return dateFormat.parse("$day $year $formattedTime")
    }

    fun isDateAfter(currentDate: Calendar, day: String, time: String, year: Int): Boolean {
        val targetDate = parseDate(day, time, year) ?: return false
        return targetDate.after(currentDate.time)
    }

    fun calculateTimeRemaining(raceDate: Date, currentDate: Calendar): String {
        val diff = raceDate.time - currentDate.timeInMillis

        val days = diff / (24 * 60 * 60 * 1000)
        val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (diff % (60 * 1000)) / 1000

        return if (days > 0) {
            "${days.toString().padStart(2, '0')}d ${
                hours.toString().padStart(2, '0')
            }h ${minutes.toString().padStart(2, '0')}m"
        } else {
            "${hours.toString().padStart(2, '0')}h ${
                minutes.toString().padStart(2, '0')
            }m ${seconds.toString().padStart(2, '0')}s"
        }
    }

    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun formatTime(time: String): String {
        val parts = time.split(":")
        val hours = parts[0].padStart(2, '0')
        val minutes = parts[1]
        return "$hours:$minutes"
    }


    fun parseSessionDate(day: String, time: String): Date {
        val year = getCurrentYear()
        return parseDate(day, time, year) ?: Date(0)
    }
}
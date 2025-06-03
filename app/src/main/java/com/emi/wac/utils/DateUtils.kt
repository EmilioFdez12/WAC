package com.emi.wac.utils

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Object containing utility functions for date-related operations.
 */
object DateUtils {
    // Initialize a SimpleDateFormat for parsing and formatting dates in "dd MMM yyyy HH:mm" format
    private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH)

    /**
     * Formats a date string from "yyyy-MM-dd'T'HH:mm:ss'Z'" (UTC) to "dd MMM yyyy" in the device's local timezone.
     * @param dateString The input date string in UTC format
     * @return Formatted date string or the original string if parsing fails
     */
    fun formatDate(dateString: String): String {
        return try {
            // Define input format for parsing UTC date strings
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Set input timezone to UTC
            // Define output format for local timezone
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Use device's local timezone
            val date = inputFormat.parse(dateString) // Parse input string to Date
            date?.let { outputFormat.format(it) } ?: dateString // Format date or return original if null
        } catch (_: Exception) {
            dateString // Return original string if parsing fails
        }
    }

    /**
     * Parses a date string constructed from day, time, and year into a Date object.
     * @param day The day part of the date (e.g., "15 Jan")
     * @param time The time in "HH:mm" format
     * @param year The year as an integer
     * @return Parsed Date object or null if parsing fails
     */
    fun parseDate(day: String, time: String, year: Int): Date? {
        val formattedTime = formatTime(time) // Ensure time is in correct "HH:mm" format
        // Set dateFormat to use device's local timezone
        dateFormat.timeZone = TimeZone.getDefault()
        // Parse combined date string
        return dateFormat.parse("$day $year $formattedTime")
    }

    /**
     * Checks if a target date is after the provided current date.
     * @param currentDate The current date as a Calendar object
     * @param day The day part of the target date
     * @param time The time part of the target date
     * @param year The year of the target date
     * @return True if the target date is after the current date, false otherwise
     */
    fun isDateAfter(currentDate: Calendar, day: String, time: String, year: Int): Boolean {
        // Parse target date or return false if null
        val targetDate = parseDate(day, time, year) ?: return false
        // Compare target date with current date
        return targetDate.after(currentDate.time)
    }

    /**
     * Calculates the time remaining between the current date and a target race date.
     * @param raceDate The target date for the race
     * @param currentDate The current date as a Calendar object
     * @return A string representing the remaining time (e.g., "02d 03h 15m" or "03h 15m 30s")
     */
    fun calculateTimeRemaining(raceDate: Date, currentDate: Calendar): String {
        // Calculate time difference in milliseconds
        val diff = raceDate.time - currentDate.timeInMillis

        // Calculate days, hours, minutes, and seconds from the difference
        val days = diff / (24 * 60 * 60 * 1000)
        val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)
        val seconds = (diff % (60 * 1000)) / 1000

        // Return formatted string based on whether days are present
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

    /**
     * Retrieves the current year from the system calendar.
     * @return The current year as an integer
     */
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    /**
     * Formats a time string to ensure it is in "HH:mm" format.
     * @param time The input time string
     * @return Formatted time string or "00:00" if input is invalid
     */
    fun formatTime(time: String): String {
        // Return default time if input is empty
        if (time.isEmpty()) return "00:00"
        // Split time into hours and minutes
        val parts = time.split(":")
        // Ensure hours is two digits
        val hours = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
        // Use minutes or default to "00"
        val minutes = parts.getOrNull(1) ?: "00"
        // Return formatted time
        return "$hours:$minutes"
    }

    /**
     * Parses a session date from day and time strings, using the current year.
     * If inputs are invalid or "TBD", returns a far-future date for sorting purposes.
     * @param day The day part of the date
     * @param time The time part of the date
     * @return Parsed Date object or a far-future date if inputs are invalid
     */
    fun parseSessionDate(day: String, time: String): Date {
        // Check for invalid or TBD inputs
        if (day.trim().isEmpty() || time.trim().isEmpty() ||
            day.equals("TBD", ignoreCase = true) || time.equals("TBD", ignoreCase = true)
        ) {
            // Return far-future date for sorting
            return Date(Long.MAX_VALUE)
        }

        val year = getCurrentYear()
        // Parse date or return far-future date
        return parseDate(day.trim(), time.trim(), year) ?: Date(Long.MAX_VALUE)
    }

    /**
     * Converts a time string in "HH:mm" format from Madrid timezone (CET/CEST) to the device's local timezone.
     * @param timeString The input time string in "HH:mm" format
     * @return The time converted to the device's local timezone or the original time if conversion fails
     */
    fun convertToLocalTime(timeString: String): String {
        try {
            // Parse the input time string
            val originalTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
            val now = ZonedDateTime.now() // Get current date-time

            // Create ZonedDateTime for Madrid timezone
            val madridTime = ZonedDateTime.of(
                now.toLocalDate(),
                originalTime,
                ZoneId.of("Europe/Madrid")
            )

            // Convert to device's local timezone
            val localTime = madridTime.withZoneSameInstant(ZoneId.systemDefault())

            // Format the converted time
            return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (_: Exception) {
            // Return original time if conversion fails
            return timeString
        }
    }
}
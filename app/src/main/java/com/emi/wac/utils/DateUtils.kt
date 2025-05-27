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
    private val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH)

    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault() // Usar zona horaria del dispositivo
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (_: Exception) {
            dateString
        }
    }

    fun parseDate(day: String, time: String, year: Int): Date? {
        val formattedTime = formatTime(time)
        // Configurar para usar la zona horaria local del dispositivo
        dateFormat.timeZone = TimeZone.getDefault()
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
        if (time.isEmpty()) return "00:00"

        val parts = time.split(":")
        val hours = parts.getOrNull(0)?.padStart(2, '0') ?: "00"
        val minutes = parts.getOrNull(1) ?: "00"
        return "$hours:$minutes"
    }

    fun parseSessionDate(day: String, time: String): Date {
        // If day or hour is "TBD" or empty, return a date far in the future
        // in order to sort it to the end of the list
        if (day.trim().isEmpty() || time.trim().isEmpty() ||
            day.equals("TBD", ignoreCase = true) || time.equals("TBD", ignoreCase = true)
        ) {
            return Date(Long.MAX_VALUE)
        }

        val year = getCurrentYear()
        return parseDate(day.trim(), time.trim(), year) ?: Date(Long.MAX_VALUE)
    }

    /**
     * Convierte una hora en formato "HH:mm" de la zona horaria de España (CET/CEST)
     * a la zona horaria local del dispositivo.
     */
    fun convertToLocalTime(timeString: String): String {
        try {
            // Parsear la hora original (asumiendo que está en zona horaria de España - Europe/Madrid)
            val originalTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))

            // Crear una fecha actual para poder aplicar la zona horaria
            val now = ZonedDateTime.now()

            // Crear un ZonedDateTime con la hora original en la zona horaria de España
            val madridTime = ZonedDateTime.of(
                now.toLocalDate(),
                originalTime,
                ZoneId.of("Europe/Madrid")
            )

            // Convertir a la zona horaria local del dispositivo
            val localTime = madridTime.withZoneSameInstant(ZoneId.systemDefault())

            // Formatear la hora local
            return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (_: Exception) {
            // En caso de error, devolver la hora original
            return timeString
        }
    }
}
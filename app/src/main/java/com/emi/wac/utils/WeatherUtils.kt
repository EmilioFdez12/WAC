package com.emi.wac.utils

import com.emi.wac.R

object WeatherUtils {
    /**
     * Returns the ID of the corresponding icon resource for the Open-Meteo weathercode.
     * @param weatherCode Climate code as returned by the Open-Meteo API
     * @return ID of the corresponding icon resource for the Open-Meteo weathercode.
     */
    fun getWeatherIcon(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.drawable.ic_sunny
            in 1..3 -> R.drawable.ic_partly_cloud
            in 51..55 -> R.drawable.ic_rain_light
            in 61..65 -> R.drawable.ic_rain
            95 -> R.drawable.ic_thunderstorm
            else -> R.drawable.ic_sunny
        }
    }

    /**
     * Returns a legible description of the weather based on the weathercode.
     * @param weatherCode Climate code as returned by the Open-Meteo API
     * @return Description of the weather based on the weathercode.
     */
    fun getWeatherDescription(weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "Cielo despejado"
            1, 2, 3 -> "Parcialmente nublado"
            51 -> "Llovizna ligera"
            53 -> "Llovizna moderada"
            55 -> "Llovizna intensa"
            61 -> "Lluvia ligera"
            63 -> "Lluvia moderada"
            65 -> "Lluvia intensa"
            71 -> "Nieve ligera"
            73 -> "Nieve moderada"
            75 -> "Nieve intensa"
            95 -> "Tormenta"
            else -> "Desconocido"
        }
    }
}
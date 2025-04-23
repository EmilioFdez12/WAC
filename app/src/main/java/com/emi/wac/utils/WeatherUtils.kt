package com.emi.wac.utils

import com.emi.wac.R

object WeatherUtils {
    /**
     * Devuelve el ID del recurso de ícono correspondiente al weathercode de Open-Meteo.
     *
     * @param weatherCode Código del clima según la API de Open-Meteo
     * @return ID del recurso de ícono en R.drawable
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
     * Devuelve una descripción legible del clima según el weathercode.
     *
     * @param weatherCode Código del clima según la API de Open-Meteo
     * @return Descripción del clima en español
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
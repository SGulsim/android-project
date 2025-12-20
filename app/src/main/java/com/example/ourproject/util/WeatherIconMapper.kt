package com.example.ourproject.util

object WeatherIconMapper {
    fun getIconName(weatherMain: String, iconCode: String): String {
        return when {
            weatherMain.contains("Clear", ignoreCase = true) || iconCode.contains("01") -> "sun"
            weatherMain.contains("Clouds", ignoreCase = true) || iconCode.contains("02") || iconCode.contains("03") || iconCode.contains("04") -> "cloud"
            weatherMain.contains("Rain", ignoreCase = true) || weatherMain.contains("Drizzle", ignoreCase = true) || iconCode.contains("09") || iconCode.contains("10") -> "rain"
            weatherMain.contains("Thunderstorm", ignoreCase = true) || iconCode.contains("11") -> "rain"
            weatherMain.contains("Snow", ignoreCase = true) || iconCode.contains("13") -> "cloud"
            weatherMain.contains("Mist", ignoreCase = true) || weatherMain.contains("Fog", ignoreCase = true) || iconCode.contains("50") -> "cloud"
            else -> "cloud"
        }
    }
}


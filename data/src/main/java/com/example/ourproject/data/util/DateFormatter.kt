package com.example.ourproject.data.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val russianLocale = Locale.forLanguageTag("ru-RU")
    
    fun formatDate(timestamp: Long, pattern: String = "EEEE, d MMMM"): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat(pattern, russianLocale)
        return sdf.format(date)
    }

    fun formatTime(timestamp: Long, pattern: String = "HH:mm"): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat(pattern, russianLocale)
        return sdf.format(date)
    }

    fun formatShortDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("d MMM", russianLocale)
        return sdf.format(date)
    }

    fun getDayName(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("EEEE", russianLocale)
        return sdf.format(date)
    }
}


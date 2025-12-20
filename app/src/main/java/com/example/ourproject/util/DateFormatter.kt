package com.example.ourproject.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatDate(timestamp: Long, pattern: String = "EEEE, MMM dd"): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun formatTime(timestamp: Long, pattern: String = "h:mm a"): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    fun formatShortDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        return sdf.format(date)
    }

    fun getDayName(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(date)
    }
}


package com.example.ourproject.data.preferences

import android.content.Context
import android.content.SharedPreferences

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

class WeatherPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "weather_preferences"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val DEFAULT_TEMPERATURE_UNIT = "CELSIUS"
    }

    fun getTemperatureUnit(): TemperatureUnit {
        val unitName = prefs.getString(KEY_TEMPERATURE_UNIT, DEFAULT_TEMPERATURE_UNIT)
        return try {
            TemperatureUnit.valueOf(unitName ?: DEFAULT_TEMPERATURE_UNIT)
        } catch (e: IllegalArgumentException) {
            TemperatureUnit.CELSIUS
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        prefs.edit().putString(KEY_TEMPERATURE_UNIT, unit.name).apply()
    }

    fun convertTemperature(celsius: Int): Int {
        return when (getTemperatureUnit()) {
            TemperatureUnit.CELSIUS -> celsius
            TemperatureUnit.FAHRENHEIT -> (celsius * 9 / 5) + 32
        }
    }

    fun getTemperatureSymbol(): String {
        return when (getTemperatureUnit()) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }
    }

    fun getTemperatureSymbolShort(): String {
        return when (getTemperatureUnit()) {
            TemperatureUnit.CELSIUS -> "°"
            TemperatureUnit.FAHRENHEIT -> "°"
        }
    }
}


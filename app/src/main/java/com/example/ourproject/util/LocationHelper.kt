package com.example.ourproject.util

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {
    suspend fun getCoordinatesFromCityName(context: Context, cityName: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(cityName, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    Pair(address.latitude, address.longitude)
                } else {
                    getDefaultCoordinates(cityName)
                }
            } catch (e: Exception) {
                getDefaultCoordinates(cityName)
            }
        }
    }

    private fun getDefaultCoordinates(cityName: String): Pair<Double, Double>? {
        val defaultCities = mapOf(
            "San Francisco" to Pair(37.7749, -122.4194),
            "New York" to Pair(40.7128, -74.0060),
            "Los Angeles" to Pair(34.0522, -118.2437),
            "Chicago" to Pair(41.8781, -87.6298),
            "Miami" to Pair(25.7617, -80.1918)
        )
        return defaultCities[cityName] ?: defaultCities["San Francisco"]
    }
}


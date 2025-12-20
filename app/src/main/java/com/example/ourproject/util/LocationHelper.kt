package com.example.ourproject.util

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {
    suspend fun getCoordinatesFromCityName(context: Context, cityName: String): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale("ru", "RU"))
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

    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
        return withContext(Dispatchers.IO) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                
                val hasFineLocation = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                
                val hasCoarseLocation = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                
                if (!hasFineLocation && !hasCoarseLocation) {
                    return@withContext null
                }
                
                val providers = locationManager.getProviders(true)
                var bestLocation: Location? = null
                
                for (provider in providers) {
                    try {
                        val location = locationManager.getLastKnownLocation(provider)
                        if (location != null) {
                            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                                bestLocation = location
                            }
                        }
                    } catch (e: SecurityException) {
                        continue
                    }
                }
                
                bestLocation?.let {
                    Pair(it.latitude, it.longitude)
                } ?: getDefaultCoordinates("Москва")
            } catch (e: Exception) {
                getDefaultCoordinates("Москва")
            }
        }
    }

    private fun getDefaultCoordinates(cityName: String): Pair<Double, Double>? {
        val defaultCities = mapOf(
            "Москва" to Pair(55.7558, 37.6173),
            "Санкт-Петербург" to Pair(59.9343, 30.3351),
            "Новосибирск" to Pair(55.0084, 82.9357),
            "Екатеринбург" to Pair(56.8431, 60.6454),
            "Казань" to Pair(55.8304, 49.0661),
            "Нижний Новгород" to Pair(56.2965, 43.9361),
            "Челябинск" to Pair(55.1644, 61.4368),
            "Самара" to Pair(53.2001, 50.15),
            "Омск" to Pair(54.9885, 73.3242),
            "Ростов-на-Дону" to Pair(47.2357, 39.7015)
        )
        return defaultCities[cityName] ?: defaultCities["Москва"]
    }
}


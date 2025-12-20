package com.example.ourproject.data.repository

import com.example.ourproject.data.api.WeatherApiClient
import com.example.ourproject.data.api.model.CurrentWeatherResponse
import com.example.ourproject.data.api.model.DailyForecastResponse

class WeatherRepository {
    private val apiService = WeatherApiClient.apiService

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        return apiService.getCurrentWeather(
            lat = lat,
            lon = lon,
            appId = WeatherApiClient.API_KEY
        )
    }

    suspend fun getDailyForecast(lat: Double, lon: Double, days: Int = 16): DailyForecastResponse {
        return apiService.getDailyForecast(
            lat = lat,
            lon = lon,
            cnt = days,
            appId = WeatherApiClient.API_KEY
        )
    }
}


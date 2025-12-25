package com.example.ourproject.network.repository

import com.example.ourproject.network.api.WeatherApiClient
import com.example.ourproject.network.api.model.CurrentWeatherResponse
import com.example.ourproject.network.api.model.ForecastResponse

class WeatherRepository(private val apiKey: String) {
    private val apiService = WeatherApiClient.create(apiKey)

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        return apiService.getCurrentWeather(
            lat = lat,
            lon = lon,
            appId = apiKey
        )
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return apiService.getForecast(
            lat = lat,
            lon = lon,
            appId = apiKey
        )
    }
}


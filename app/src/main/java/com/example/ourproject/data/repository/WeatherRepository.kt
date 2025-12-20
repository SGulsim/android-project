package com.example.ourproject.data.repository

import com.example.ourproject.data.api.WeatherApiClient
import com.example.ourproject.data.api.model.CurrentWeatherResponse
import com.example.ourproject.data.api.model.ForecastResponse

class WeatherRepository {
    private val apiService = WeatherApiClient.apiService

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        return apiService.getCurrentWeather(
            lat = lat,
            lon = lon,
            appId = WeatherApiClient.API_KEY
        )
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return apiService.getForecast(
            lat = lat,
            lon = lon,
            appId = WeatherApiClient.API_KEY
        )
    }
}


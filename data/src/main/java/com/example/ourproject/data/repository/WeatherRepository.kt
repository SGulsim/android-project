package com.example.ourproject.data.repository

import com.example.ourproject.network.repository.WeatherRepository as NetworkWeatherRepository

class WeatherRepository(private val apiKey: String) {
    private val networkRepository = NetworkWeatherRepository(apiKey)

    suspend fun getCurrentWeather(lat: Double, lon: Double) = networkRepository.getCurrentWeather(lat, lon)

    suspend fun getForecast(lat: Double, lon: Double) = networkRepository.getForecast(lat, lon)
}


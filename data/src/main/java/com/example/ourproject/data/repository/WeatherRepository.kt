package com.example.ourproject.data.repository

import com.example.ourproject.data.api.model.*
import com.example.ourproject.network.repository.WeatherRepository as NetworkWeatherRepository

import android.util.Log

class WeatherRepository(private val apiKey: String) {
    private val networkRepository = NetworkWeatherRepository(apiKey)

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        try {
            if (apiKey.isBlank()) {
                throw IllegalStateException("API key is empty. Please set WEATHER_API_KEY in local.properties")
            }
            Log.d("WeatherRepository", "Getting current weather for lat=$lat, lon=$lon")
            val networkResponse = networkRepository.getCurrentWeather(lat, lon)
            Log.d("WeatherRepository", "Current weather received: ${networkResponse.name}, temp=${networkResponse.main.temp}")
            return networkResponse.toDataModel()
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error getting current weather", e)
            throw e
        }
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        try {
            if (apiKey.isBlank()) {
                throw IllegalStateException("API key is empty. Please set WEATHER_API_KEY in local.properties")
            }
            Log.d("WeatherRepository", "Getting forecast for lat=$lat, lon=$lon")
            val networkResponse = networkRepository.getForecast(lat, lon)
            Log.d("WeatherRepository", "Forecast received: cod=${networkResponse.cod}, list size=${networkResponse.list.size}")
            return networkResponse.toDataModel()
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error getting forecast", e)
            throw e
        }
    }
}

// Extension functions to convert network models to data models
private fun com.example.ourproject.network.api.model.CurrentWeatherResponse.toDataModel(): CurrentWeatherResponse {
    return CurrentWeatherResponse(
        coord = Coord(coord.lon, coord.lat),
        weather = weather.map { Weather(it.id, it.main, it.description, it.icon) },
        base = base,
        main = Main(main.temp, main.feelsLike, main.tempMin, main.tempMax, main.pressure, main.humidity),
        visibility = visibility,
        wind = Wind(wind.speed, wind.deg, wind.gust),
        rain = rain?.let { Rain(it.oneHour, it.threeH) },
        clouds = Clouds(clouds.all),
        dt = dt,
        sys = Sys(sys.type, sys.id, sys.country, sys.sunrise, sys.sunset),
        timezone = timezone,
        id = id,
        name = name,
        cod = cod
    )
}

private fun com.example.ourproject.network.api.model.ForecastResponse.toDataModel(): ForecastResponse {
    return ForecastResponse(
        cod = cod,
        message = message,
        cnt = cnt,
        list = list.map { it.toDataModel() },
        city = ForecastCity(
            city.id,
            city.name,
            Coord(city.coord.lon, city.coord.lat),
            city.country,
            city.population,
            city.timezone,
            city.sunrise,
            city.sunset
        )
    )
}

private fun com.example.ourproject.network.api.model.ForecastItem.toDataModel(): ForecastItem {
    return ForecastItem(
        dt = dt,
        main = MainForecast(
            main.temp,
            main.feelsLike,
            main.tempMin,
            main.tempMax,
            main.pressure,
            main.seaLevel,
            main.grndLevel,
            main.humidity,
            main.tempKf
        ),
        weather = weather.map { Weather(it.id, it.main, it.description, it.icon) },
        clouds = Clouds(clouds.all),
        wind = Wind(wind.speed, wind.deg, wind.gust),
        visibility = visibility,
        pop = pop,
        rain = rain?.let { Rain(it.oneHour, it.threeH) },
        snow = snow?.let { networkSnow -> Snow(networkSnow.threeH) },
        sys = SysForecast(sys.pod),
        dtTxt = dtTxt
    )
}


package com.example.ourproject.data.repository

import com.example.ourproject.data.api.model.*
import com.example.ourproject.network.repository.WeatherRepository as NetworkWeatherRepository

class WeatherRepository(private val apiKey: String) {
    private val networkRepository = NetworkWeatherRepository(apiKey)

    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeatherResponse {
        val networkResponse = networkRepository.getCurrentWeather(lat, lon)
        return networkResponse.toDataModel()
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        val networkResponse = networkRepository.getForecast(lat, lon)
        return networkResponse.toDataModel()
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
        snow = snow?.let { Snow(snow.threeH) },
        sys = SysForecast(sys.pod),
        dtTxt = dtTxt
    )
}


package com.example.ourproject.data.api

import com.example.ourproject.data.api.model.CurrentWeatherResponse
import com.example.ourproject.data.api.model.DailyForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): CurrentWeatherResponse

    @GET("data/2.5/forecast/daily")
    suspend fun getDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") cnt: Int = 16,
        @Query("appid") appId: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): DailyForecastResponse
}


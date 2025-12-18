package com.example.ourproject.data.repository

import com.example.ourproject.data.dao.ForecastDao
import com.example.ourproject.data.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

class ForecastRepository(private val forecastDao: ForecastDao) {
    fun getAllForecasts(): Flow<List<ForecastEntity>> = forecastDao.getAllForecasts()

    suspend fun getForecastById(id: Long): ForecastEntity? = forecastDao.getForecastById(id)

    suspend fun insertForecast(forecast: ForecastEntity): Long = forecastDao.insertForecast(forecast)

    suspend fun insertAllForecasts(forecasts: List<ForecastEntity>) = forecastDao.insertAllForecasts(forecasts)

    suspend fun updateForecast(forecast: ForecastEntity) = forecastDao.updateForecast(forecast)

    suspend fun deleteForecast(forecast: ForecastEntity) = forecastDao.deleteForecast(forecast)

    suspend fun deleteAllForecasts() = forecastDao.deleteAllForecasts()
}


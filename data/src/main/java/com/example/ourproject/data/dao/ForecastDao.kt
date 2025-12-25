package com.example.ourproject.data.dao

import androidx.room.*
import com.example.ourproject.data.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecasts ORDER BY id ASC")
    fun getAllForecasts(): Flow<List<ForecastEntity>>

    @Query("SELECT * FROM forecasts WHERE id = :id")
    suspend fun getForecastById(id: Long): ForecastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForecasts(forecasts: List<ForecastEntity>)

    @Update
    suspend fun updateForecast(forecast: ForecastEntity)

    @Delete
    suspend fun deleteForecast(forecast: ForecastEntity)

    @Query("DELETE FROM forecasts")
    suspend fun deleteAllForecasts()
}


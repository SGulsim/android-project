package com.example.ourproject.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ourproject.data.dao.ForecastDao
import com.example.ourproject.data.dao.LocationDao
import com.example.ourproject.data.entity.ForecastEntity
import com.example.ourproject.data.entity.LocationEntity

@Database(
    entities = [ForecastEntity::class, LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


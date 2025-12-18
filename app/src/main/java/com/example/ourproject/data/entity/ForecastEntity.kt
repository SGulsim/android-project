package com.example.ourproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecasts")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val day: String,
    val date: String,
    val highTemp: Int,
    val lowTemp: Int,
    val precipitation: Int,
    val iconName: String = "cloud"
)


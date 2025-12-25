@file:Suppress("unused")

package com.example.ourproject.data.api.model

import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lon") val lon: Double,
    @SerializedName("lat") val lat: Double
)

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int,
    @SerializedName("gust") val gust: Double? = null
)

data class Rain(
    @SerializedName("1h") val oneHour: Double? = null,
    @SerializedName("3h") val threeH: Double? = null
)

data class Clouds(
    @SerializedName("all") val all: Int
)


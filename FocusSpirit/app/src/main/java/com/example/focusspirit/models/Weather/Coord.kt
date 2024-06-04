package com.example.focusspirit.models.Weather

import com.google.gson.annotations.SerializedName

//TODO
// Create data class Coord (Refer to API Response)
// Hint: Refer to Wind Data Class

data class Coord(
    @SerializedName("lon") val lon: Double,
    @SerializedName("lat") val lat: Double
)

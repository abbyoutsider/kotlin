package com.example.focusspirit.models.Weather

import com.google.gson.annotations.SerializedName

//TODO
// Create data class Main (Refer to API Response)
// Hint: Refer to Wind Data Class

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feels_like: Double,
    @SerializedName("temp_min") val temp_min: Double,
    @SerializedName("temp_max") val temp_max: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)
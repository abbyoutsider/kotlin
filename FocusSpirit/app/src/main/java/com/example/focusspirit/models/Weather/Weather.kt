package com.example.focusspirit.models.Weather
import com.google.gson.annotations.SerializedName

//TODO
// Create data class WeatherItem (Refer to API Response)
// Hint: Refer to Wind Data Class

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
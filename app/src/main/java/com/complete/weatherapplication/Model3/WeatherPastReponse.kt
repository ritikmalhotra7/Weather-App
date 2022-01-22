package com.complete.weatherapplication.Model3

data class WeatherPastReponse(
    val current: Current?,
    val hourly: List<Hourly>?,
    val lat: Double?,
    val lon: Double?,
    val timezone: String?,
    val timezone_offset: Int?
)
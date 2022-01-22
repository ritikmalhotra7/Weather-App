package com.complete.weatherapplication.Api

import com.complete.weatherapplication.Model.WeatherResponse
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.Model2.WeatherReportResponse
import com.complete.weatherapplication.Utils.Utils.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/data/2.5/weather")
    suspend fun getSearchedLocationInfo(
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("units") units: String,
        @Query("appid") appid: String = API_KEY
    ): Response<WeatherResponse>
    @GET("data/2.5/onecall")
    suspend fun getForecast(
        @Query("lon")
        longitude:Double,
        @Query("lat")
        latitude:Double,
        @Query("units")
        units:String = "default",
        @Query("exclude")
        exclude:String = "minutely,hourly",
        @Query("appid")
        apikey:String = API_KEY
    ):Response<WeatherReportResponse>
}
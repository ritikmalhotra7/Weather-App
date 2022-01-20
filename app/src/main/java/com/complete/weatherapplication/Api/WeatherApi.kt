package com.complete.weatherapplication.Api

import com.complete.weatherapplication.Model.WeatherResponse
import com.complete.weatherapplication.Utils.Utils.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getSearchedLocationInfo(
        @Query("q")
        cityName : String,
        @Query("appid")
        apiKey:String=API_KEY
    ): Response<com.complete.weatherapplication.Model.WeatherResponse>
    @GET("data/2.5/onecall")
    suspend fun getForecast(
        @Query("lon")
        longitude:String,
        @Query("lat")
        latitude:String,
        @Query("units")
        units:String = "default",
        @Query("appid")
        apikey:String = API_KEY
    )
}
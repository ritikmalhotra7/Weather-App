package com.complete.weatherapplication.api

import com.complete.weatherapplication.model.WeatherResponse
import com.complete.weatherapplication.model2.WeatherReportResponse
import com.complete.weatherapplication.model3.WeatherPastReponse
import com.complete.weatherapplication.utils.Utils.Companion.API_KEY
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
    @GET("data/2.5/onecall/timemachine")
    suspend fun getPastCall(
        @Query("lon")
        longitude:Double,
        @Query("lat")
        latitude:Double,
        @Query("units")
        units:String = "default",
        @Query("dt")
        dt:Int ,
        @Query("exclude")
        exclude:String = "minutely,hourly",
        @Query("appid")
        apikey:String = API_KEY
    ):Response<WeatherPastReponse>
}
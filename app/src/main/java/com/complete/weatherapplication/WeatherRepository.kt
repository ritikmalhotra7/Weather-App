package com.complete.weatherapplication

import com.complete.weatherapplication.Api.RetrofitInstance
import retrofit2.Retrofit

class WeatherRepository {
    suspend fun getSearched(lon :String,lat:String) = RetrofitInstance.api.getSearchedLocationInfo(lon,lat,"metric")
    suspend fun getForecast(lon:Double,lat:Double) = RetrofitInstance.api.getForecast(lon,lat,"metric")
}
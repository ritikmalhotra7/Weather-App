package com.complete.weatherapplication

import com.complete.weatherapplication.Api.RetrofitInstance
import retrofit2.Retrofit

class WeatherRepository {
    suspend fun getSearched(name :String) = RetrofitInstance.api.getSearchedLocationInfo(name)
    suspend fun getForecast(lon:String,lat:String,units:String) = RetrofitInstance.api.getForecast(lon,lat,units)
}
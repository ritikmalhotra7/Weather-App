package com.complete.weatherapplication

import com.complete.weatherapplication.Api.RetrofitInstance
import retrofit2.Retrofit

class WeatherRepository {
    suspend fun getSearched(lon :Double,lat:Double,unit:String) = RetrofitInstance.api.getSearchedLocationInfo(lon,lat,unit)
    suspend fun getForecast(lon:Double,lat:Double,unit:String) = RetrofitInstance.api.getForecast(lon,lat,unit)
    suspend fun getPastCall(lon:Double,lat:Double,unit:String,date:Int)  = RetrofitInstance.api.getPastCall(lon,lat,unit,date)
}
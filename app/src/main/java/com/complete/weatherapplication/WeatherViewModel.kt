package com.complete.weatherapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.complete.weatherapplication.Utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import com.complete.weatherapplication.Model.WeatherResponse
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.Model2.WeatherReportResponse

class WeatherViewModel(val repo:WeatherRepository): ViewModel() {
    val reportList:MutableLiveData<Resources<WeatherReportResponse>> = MutableLiveData()
    var search :MutableLiveData<Resources<WeatherResponse>> = MutableLiveData()


    fun getSearch(lon:String,lat:String) = viewModelScope.launch {
        search.postValue(Resources.Loading())
        val searched = repo.getSearched(lon,lat)
        search.postValue(handleWeatherResponse(searched))
    }
    private fun handleWeatherResponse(response: Response<WeatherResponse>):Resources<com.complete.weatherapplication.Model.WeatherResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }
    fun getForecast(lon:Double,lat:Double) = viewModelScope.launch {
        reportList.postValue(Resources.Loading())
        val reports = repo.getForecast(lon,lat)
        reportList.postValue(handleDailyResponse(reports))
    }
    private fun handleDailyResponse(response: Response<WeatherReportResponse>):Resources<WeatherReportResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


}
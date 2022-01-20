package com.complete.weatherapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.complete.weatherapplication.Utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import com.complete.weatherapplication.Model.WeatherResponse

class WeatherViewModel(val repo:WeatherRepository): ViewModel() {
    val reportList:MutableLiveData<Resources<com.complete.weatherapplication.Model.WeatherResponse>> = MutableLiveData()
    val searched :Response<WeatherResponse>? = null


    fun getSearch(name:String) = viewModelScope.launch {
        reportList.postValue(Resources.Loading())
        val searched = repo.getSearched(name)
        reportList.postValue(handleBreakingWeatherResponse(searched))
    }
    private fun handleBreakingWeatherResponse(response: Response<com.complete.weatherapplication.Model.WeatherResponse>):Resources<com.complete.weatherapplication.Model.WeatherResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->

                // breakingNewsResponse ?: resultResponse = this indicates that if
                // breakingNewsRespponse is not null then it will be returned and
                // if its null then only resultResponse is returned
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


}
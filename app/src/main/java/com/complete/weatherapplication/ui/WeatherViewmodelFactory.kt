package com.complete.weatherapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.complete.weatherapplication.WeatherRepository

class WeatherViewmodelFactory(val repo: WeatherRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherViewModel(repo) as T
    }
}
package com.complete.weatherapplication.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.complete.weatherapplication.R
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
import com.complete.weatherapplication.databinding.FragmentCurrentBinding
import java.util.*
import androidx.lifecycle.Observer
import com.complete.weatherapplication.Utils.Resources
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import java.text.SimpleDateFormat

import coil.load


class CurrentFragment : Fragment(R.layout.fragment_current) {

    private val REQUEST_CODE: Int = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var provider: String? = null

    private var _binding:FragmentCurrentBinding? = null
    val binding : FragmentCurrentBinding get() = _binding!!
    lateinit var viewModel:WeatherViewModel
    val unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
    private var cityName: String? = null
    var latitude : Double = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("latitude","0.0")?.toDouble()?:0.0
    var longitude : Double = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("longitude","0.0")?.toDouble()?:0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentBinding.inflate(inflater,container,false)
        showProgressBar()
        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        binding.username.text = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("name","")

        getData(latitude,longitude)

        binding.see7dayReport.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(CurrentFragmentDirections.actionCurrentFragmentToReportfragment(cityName.toString()))
        }


        return binding.root
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    fun getData(latitude : Double, longitude:Double) {
        viewModel.getSearch(longitude,latitude,unit.toString())
        viewModel.search.observe(viewLifecycleOwner, Observer{response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data?.let{
                        binding.location.text = it.name
                        binding.locationName.text = "${it.name},${it.sys.country}"
                        cityName = it.name
                        val dt = it.dt.toString()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        val icon = it.weather[0].icon
                        binding.ivCondition.load("https://openweathermap.org/img/wn/$icon@4x.png"){
                            crossfade(true)
                        }
                        binding.condition.text = it.weather[0].main
                        binding.humidity.text = "Humidity - " + it.main.humidity.toString()+"%"
                        binding.windspeed.text = "WindSpeed - ${it.wind.speed} M/Sec"
                        binding.preasure.text = "Preasure - ${it.main.pressure} hPa"
                        if(unit == "metric"){
                            binding.temperaturemax.text = "Temp(max) - ${it.main.temp_max}°C"
                            binding.temperaturemin.text = "Temp(min) - ${it.main.temp_min}°C"
                            val str = it.main.temp.toString().substring(0,2)
                            binding.temperature.text = "$str°C"
                        }else if(unit == "imperial"){
                            binding.temperaturemax.text = "Temp(max) - ${it.main.temp_max}°F"
                            binding.temperaturemin.text = "Temp(min) - ${it.main.temp_min}°F"
                            val str = it.main.temp.toString().substring(0,2)
                            binding.temperature.text = "$str°F"
                        }
                        binding.visibility.text = "Visibility - ${it.visibility/1000} KM"
                        activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.edit()?.apply {
                            putString("cityname",cityName)
                            apply()
                        }
                    }
                }
                is Resources.Error ->{
                    hideProgressBar()
                    response.data?.let{
                        Toast.makeText(activity,"An Error occured $it",Toast.LENGTH_SHORT).show()
                    }
                }
                is Resources.Loading ->{
                    showProgressBar()
                }
            }
        })
    }
}
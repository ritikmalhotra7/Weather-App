package com.complete.weatherapplication.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import com.complete.weatherapplication.Adapters.WeatherReportAdapter
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.R
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
import com.complete.weatherapplication.databinding.FragmentCurrentBinding
import java.io.IOException
import java.util.*

class CurrentFragment : Fragment() {

    private var _binding:FragmentCurrentBinding? = null
    val binding : FragmentCurrentBinding get() = _binding!!

    private var cityName: String? = null
    private var currentLocation : Location? = null
    private lateinit var locationManager: LocationManager
    private val PERMISSION_CODE = 1

    lateinit var viewModel:WeatherViewModel
    lateinit var adapter:WeatherReportAdapter
    lateinit var list:List<Daily>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentBinding.inflate(inflater,container,false)

        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        currentLocation = getLongitudeLatitude()
        cityName = getCityName(currentLocation!!.longitude,currentLocation!!.latitude)
        binding.location.text = cityName
        viewModel.getSearch(cityName!!)
        viewModel.searched.let {
            binding.apply {
                humidity.text = it?.body()?.main!!.humidity.toString()
            }
        }

        return binding.root
    }
    fun setupAdapter(){
        adapter = WeatherReportAdapter(list)
        binding
    }
    fun getLongitudeLatitude(): Location?{
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_CODE
            )
        }
        currentLocation =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        return currentLocation
    }
    fun getCityName(longitude: Double, latitude: Double): String? {
        var cityName: String? = "Not found"
        val gcd = Geocoder(activity, Locale.getDefault())
        try {
            val addresses = gcd.getFromLocation(latitude, longitude, 10)
            for (adr in addresses) {
                if (adr != null) {
                    val city = adr.locality
                    if (city != null) {
                        if (city != "") {
                            cityName = city
                        } else {
                            Toast.makeText(activity,"notFound", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName
    }

}
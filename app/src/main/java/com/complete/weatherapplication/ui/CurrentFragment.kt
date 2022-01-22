package com.complete.weatherapplication.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.support.v4.app.*
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
import java.util.*
import androidx.lifecycle.Observer
import com.complete.weatherapplication.Utils.Resources
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.complete.weatherapplication.Utils.Utils.Companion.BASE_URL
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.OnSuccessListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import android.location.LocationManager

import com.complete.weatherapplication.MainActivity

import androidx.core.content.ContextCompat.getSystemService
import androidx.annotation.NonNull
import java.text.DateFormat


class CurrentFragment : Fragment(R.layout.fragment_current) {

    private val fusedLocationClient: FusedLocationProviderClient? = null
    private var unit: String? = "metric"
    private var provider: String? = null

    private var _binding:FragmentCurrentBinding? = null
    val binding : FragmentCurrentBinding get() = _binding!!


    lateinit var viewModel:WeatherViewModel

    private val PERMISSION_CODE = 1
    private var cityName: String? = null
    var latitude : Double = 0.0
    var longitude : Double = 0.0

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
        unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
        getCurrentLocation()
        Log.d("taget",longitude.toString()+","+latitude.toString())
        viewModel.getSearch(longitude!!,latitude!!,unit.toString())
        observeEverything()
        binding.username.text = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("name","")

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
    fun observeEverything(){
        viewModel.search.observe(viewLifecycleOwner, Observer{response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data?.let{
                        binding.location.text = it.name
                        binding.locationName.text = "${it.name},${it.sys.country}"
                        cityName = it.name
                        val dt = it.dt.toString()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        val icon = it.weather[0].icon
                        Glide.with(this).load("https://openweathermap.org/img/wn/$icon@4x.png").into(binding.ivCondition)
                        binding.condition.text = it.weather[0].main
                        binding.humidity.text = it.main.humidity.toString()+"%"
                        binding.windspeed.text = "${it.wind.speed} Meter/Sec"
                        binding.preasure.text = "${it.main.pressure} hPa"
                        if(unit == "metric"){
                            binding.temperaturemax.text = "${it.main.temp_max}°C"
                            binding.temperaturemin.text = "${it.main.temp_min}°C"
                        }else if(unit == "imperial"){
                            binding.temperaturemax.text = "${it.main.temp_max}°F"
                            binding.temperaturemin.text = "${it.main.temp_min}°F"
                        }
                        binding.visibility.text = "${it.visibility/1000} KM"
                        saveToSharedPrefs("cityName",cityName.toString())
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

    private fun saveToSharedPrefs(key: String, value: String) {
        val sh = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.edit().apply{
            putString(key,value)
            apply()
        }
    }

    fun getCurrentLocation() {
        val context = context
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permission hasn't been granted
            setDefaultLocation()
            updateSharedPreference()
            getLocationFromSharedPreference()
        } else {
            // Permission has been granted
            fusedLocationClient!!.lastLocation
                .addOnSuccessListener(requireActivity()) { location: Location? ->
                    if (location != null) {

                        // Fetching location using fusedLocationClient
                        latitude = location.latitude
                        longitude = location.longitude
                        //Toast.makeText(getContext(), latitude+" $ " + longitude, Toast.LENGTH_SHORT).show();
                        /**If location isn't fetched then set the default latitude and longitude  */
                        if (latitude == null) {
                            setDefaultLocation()
                        }

                        // updating the shared preference with latitude and longitude
                        updateSharedPreference()
                    }
                    getLocationFromSharedPreference()
                }
        }
    }

    /**Function to set the default latitude and longitude */
    fun setDefaultLocation() {
        latitude = 28.667823
        longitude = 77.114950
    }

    /**Function to update the value of latitude & longitude in sharedPreference */
    fun updateSharedPreference() {
        val editor: SharedPreferences.Editor = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.edit()
        if (latitude == null) {
            setDefaultLocation()
        }
        editor.putFloat("latitude", latitude.toFloat())
        editor.putFloat("longitude", longitude.toFloat())
        editor.apply()
    }

    /**Fetching and setting the data members to latitude & longitude */
    fun getLocationFromSharedPreference() {
        val defLocation = 0.0
        latitude = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.getFloat("latitude", defLocation.toFloat()).toDouble()
        longitude = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.getFloat("longitude", defLocation.toFloat()).toDouble()
    }
}
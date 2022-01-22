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
import com.complete.weatherapplication.Utils.Utils.Companion.BASE_URL
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import android.location.LocationManager

import com.complete.weatherapplication.MainActivity

import androidx.core.content.ContextCompat.getSystemService
import androidx.annotation.NonNull
import coil.load
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
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        val icon = it.weather[0].icon
                        binding.ivCondition.load("https://openweathermap.org/img/wn/$icon@4x.png"){
                            crossfade(true)
                        }
                        binding.condition.text = it.weather[0].main
                        binding.humidity.text = it.main.humidity.toString()+"%"
                        binding.windspeed.text = "${it.wind.speed} Meter/Sec"
                        binding.preasure.text = "${it.main.pressure} hPa"
                        if(unit == "metric"){
                            binding.temperaturemax.text = "${it.main.temp_max}°C"
                            binding.temperaturemin.text = "${it.main.temp_min}°C"
                            val str = it.main.temp.toString().substring(0,2)
                            binding.temperature.text = "$str°C"
                        }else if(unit == "imperial"){
                            binding.temperaturemax.text = "${it.main.temp_max}°F"
                            binding.temperaturemin.text = "${it.main.temp_min}°F"
                            binding.temperature.text = "${it.main.temp}°F"
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
        activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.edit().apply{
            putString(key,value)
            apply()
        }
    }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                Log.d("taget",true.toString())
                /*setDefaultLocation()*/
                updateSharedPreference()
                getLocationFromSharedPreference()

            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                fusedLocationClient!!.lastLocation
                    .addOnSuccessListener(requireActivity()) { location: Location? ->
                        location?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                        }
                        updateSharedPreference()
                        getLocationFromSharedPreference()
                    }
            }
        }
        /*if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("taget",true.toString())
            setDefaultLocation()
            updateSharedPreference()
            getLocationFromSharedPreference()
        } else {
            fusedLocationClient!!.lastLocation
                .addOnSuccessListener(requireActivity()) { location: Location? ->
                    location.let {
                        latitude = location!!.latitude
                        longitude = location.longitude
                    }?:setDefaultLocation()
                    updateSharedPreference()
                    getLocationFromSharedPreference()
                }
        }*/
        viewModel.getSearch(longitude, latitude,unit.toString())
        observeEverything()
    }
   /* override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }*/
   /* fun setDefaultLocation() {
        latitude = 28.667823
        longitude = 77.114950
    }
*/
    fun updateSharedPreference() {
        val editor: SharedPreferences.Editor = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.edit()
        if (latitude == null) {
            /*setDefaultLocation()*/
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }


    }
}
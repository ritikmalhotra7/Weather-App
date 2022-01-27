package com.complete.weatherapplication.ui.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.complete.weatherapplication.R
import com.complete.weatherapplication.ui.WeatherRepository
import com.complete.weatherapplication.ui.WeatherViewModel
import com.complete.weatherapplication.ui.WeatherViewmodelFactory
import com.complete.weatherapplication.databinding.FragmentCurrentBinding
import java.util.*
import androidx.lifecycle.Observer
import com.complete.weatherapplication.utils.Resources
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import java.text.SimpleDateFormat

import com.complete.weatherapplication.model.WeatherResponse
import com.complete.weatherapplication.utils.Utils
import com.complete.weatherapplication.utils.Utils.Companion.SHARED
import com.complete.weatherapplication.utils.Utils.Companion.isOnline


class CurrentFragment : Fragment(R.layout.fragment_current) {

    private val REQUEST_CODE: Int = 1
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var provider: String? = null

    private var _binding:FragmentCurrentBinding? = null
    val binding : FragmentCurrentBinding get() = _binding!!
    lateinit var viewModel: WeatherViewModel
    var unit = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("unit","metric")
    private var cityName: String? = null
    var latitude : Double = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("latitude","0.0")?.toDouble()?:28.6128
    var longitude : Double = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("longitude","0.0")?.toDouble()?:77.2311

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflating the binding
        _binding = FragmentCurrentBinding.inflate(inflater,container,false)
        showProgressBar()
        //getting info from shared prefs
        binding.username.text = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("name","")
        //creating object for repository
        val repo = WeatherRepository()
        //creating object for viewModelFactory
        val factory = WeatherViewmodelFactory(repo)
        //initialising the view model
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        //checking internet connection
        if(isOnline(requireActivity())){
            getData(latitude,longitude)
        }else{
            Toast.makeText(activity,"No Internet Connection!",Toast.LENGTH_SHORT).show()
        }
        //checking on click
        binding.swipeToRefresh.setOnRefreshListener {
            if(isOnline(requireActivity())){
                getData(latitude,longitude)
            }else{
                Toast.makeText(activity,"No Internet Connection!",Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(activity,"refreshed",Toast.LENGTH_SHORT).show()
            binding.swipeToRefresh.isRefreshing = false
        }
        //checking on click
        binding.see7dayReport.setOnClickListener {
            //navigating to different fragment
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_currentFragment_to_reportfragment)
        }

        return binding.root
    }
//hide progress bar
    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }
    //show progress bar
    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    //getting views from viewmodel
    fun getData(latitude : Double, longitude:Double) {
        unit = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("unit","metric")
        viewModel.getSearch(longitude,latitude,unit.toString())
        viewModel.search.observe(viewLifecycleOwner, Observer{response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data?.let{
                        setViews(it)
                        activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.edit()?.apply {
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
    //setting up the views from view model
    fun setViews(it : WeatherResponse){
        binding.location.text = it.name
        binding.locationName.text = "${it.name},${it.sys.country}"
        cityName = it.name

        val dt = it.dt.toString()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val date  = Date(dt.toLong() * 1000)
        sdf.format(date)
        val start = date.toString().substring(0,10)
        val end = date.toString().substring(30)
        val time = date.toString().substring(10,date.toString().length-14)
        binding.text2.text = start+" "+end+" "+time

        val icon = it.weather[0].icon
        binding.ivCondition.load("https://openweathermap.org/img/wn/$icon@4x.png"){
            crossfade(true)
        }

        binding.condition.text = it.weather[0].main
        binding.humidity.text = "Humidity - " + it.main.humidity.toString()+"%"
        binding.windspeed.text = "WindSpeed - ${it.wind.speed} M/Sec"
        binding.preasure.text = "Preasure - ${it.main.pressure} hPa"
        binding.visibility.text = "Visibility - ${it.visibility/1000} KM"

        if(unit == "metric"){
            binding.temperaturemax.text = "Temp(max) - ${it.main.temp_max}°C"
            binding.temperaturemin.text = "Temp(min) - ${it.main.temp_min}°C"
            val str = it.main.temp.toString().substring(0,2)
            binding.temperature.text = "$str°C"
        }else if(unit == "imperial") {
            binding.temperaturemax.text = "Temp(max) - ${it.main.temp_max}°F"
            binding.temperaturemin.text = "Temp(min) - ${it.main.temp_min}°F"
            val str = it.main.temp.toString().substring(0, 2)
            binding.temperature.text = "$str°F"
        }
    }
    //checking the permissions for location
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                /** Alert Dialogue Box to Ask the user for location permission  */
                AlertDialog.Builder(requireContext())
                    .setTitle("Required Location Permission")
                    .setMessage("Grant Permission To Use Current Location, Else default Location will be used")
                    .setPositiveButton("ok") { dialogInterface: DialogInterface?, i: Int ->

                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_CODE
                        )
                        // permission granted
                    }
                    .setNegativeButton("cancel") { dialogInterface: DialogInterface?, i: Int ->
                        // permission not granted
                        Toast.makeText(
                            activity,
                            "Kindly Grant Permission to procceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.create().show()
            } else {
                // request the permission
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )

            }
        }else{
            getCurrentLocation()
        }
    }
//getting the longitude and latitude
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permission hasn't been granted
            checkLocationPermission();
        } else {
            //permission granted
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                it.let {
                    longitude = it.longitude
                    latitude = it.latitude
                    Log.d(Utils.TAG +"3",longitude.toString())
                    Log.d(Utils.TAG +"3",latitude.toString())
                    activity?.getSharedPreferences(SHARED, Context.MODE_PRIVATE)?.edit()?.apply{
                        putString("longitude",longitude.toString())
                        putString("latitude",latitude.toString())
                        apply()
                    }
                }
            }
        }
    }
    //null the binding
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
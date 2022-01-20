package com.complete.weatherapplication.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import androidx.lifecycle.Observer
import com.complete.weatherapplication.Utils.Resources
import android.location.Criteria
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.complete.weatherapplication.Utils.Utils.Companion.BASE_URL


class CurrentFragment : Fragment(R.layout.fragment_current) {

    private var provider: String? = null

    private var _binding:FragmentCurrentBinding? = null
    val binding : FragmentCurrentBinding get() = _binding!!


    lateinit var viewModel:WeatherViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_CODE = 1
    private var cityName: String? = null
    private var currentLocation : Location? = null
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentBinding.inflate(inflater,container,false)
        showProgressBar()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocation()
        /*getCityName(28.664934, 77.142873)
        Log.d("taget",cityName.toString())*/


        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        viewModel.getSearch(77.142873.toString(),28.664934.toString())
        observeEverything()

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
    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                }
            }

    }
    fun observeEverything(){
        viewModel.search.observe(viewLifecycleOwner, Observer{response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data?.let{
                        val c = Calendar.getInstance()
                        val date = c.get(Calendar.DATE)
                        val month = c.get(Calendar.MONTH)+1
                        val year = c.get(Calendar.YEAR)
                        var hour = c.get(Calendar.HOUR_OF_DAY)
                        val minute = c.get(Calendar.MINUTE)
                        var timeStamp = ""
                        var am = "am"
                        if(minute<10 ){
                            if(hour == 0){
                                hour = 12
                            }
                            if(hour>12){
                                hour -= 12
                                am = "pm"
                            }
                            timeStamp = "$hour:0$minute $am"
                        }else {
                            if (hour == 0) {
                                hour = 12
                            }
                            if (hour > 12) {
                                hour -= 12
                                am = "pm"
                            }
                            timeStamp = "$hour:$minute $am"
                        }
                        binding.text2.text = "$timeStamp - $date/$month/$year"
                        Log.d("taget", it.name

                        )
                        binding.location.text = it.name
                        binding.locationName.text = "${it.name},${it.sys.country}"
                        cityName = it.name
                        val icon = it.weather[0].icon
                        Glide.with(this).load("https://openweathermap.org/img/wn/$icon@4x.png").into(binding.ivCondition)
                        binding.condition.text = it.weather[0].main
                        binding.humidity.text = it.main.humidity.toString()+"%"
                        binding.windspeed.text = "${it.wind.speed} Meter/Sec"
                        binding.preasure.text = "${it.main.pressure} hPa"
                        binding.temperature.text = "${it.main.temp}°C(${it.main.temp_max} - ${it.main.temp_min})"
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
    fun getLocation(){
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val c = Criteria()
        provider = locationManager.getBestProvider(c, false)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            currentLocation = locationManager.getLastKnownLocation(provider!!)
        }

        if (currentLocation != null) {
            val lng: Double = currentLocation!!.getLongitude()
            val lat: Double = currentLocation!!.getLatitude()
            Log.d("taget",lng.toString())
            Log.d("taget",lat.toString())
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Permision Granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Please Provide the Permissions", Toast.LENGTH_SHORT).show()
        }
    }
}
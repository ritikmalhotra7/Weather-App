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
import java.text.SimpleDateFormat


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
    var latitude : Double = 28.664934
    var longitude : Double = 77.142873

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
        getLocation()
        viewModel.getSearch(77.142873,28.664934)
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
                        binding.temperature.text = "${it.main.temp}°C(${it.main.temp_max} - ${it.main.temp_min})"

                        saveToSharedPrefrences("cityName",cityName.toString())
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
    fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date? {
        val parser  = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
    fun saveToSharedPrefrences(key:String,value:String){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        sharedPref.edit().apply{
            putString(key,value)
            commit()
        }
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
            longitude = currentLocation!!.getLongitude()
            latitude = currentLocation!!.getLatitude()
            saveToSharedPrefrences("latitude",latitude.toString())
            saveToSharedPrefrences("longitude",longitude.toString())
            Log.d("taget",longitude.toString())
            Log.d("taget",latitude.toString())
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
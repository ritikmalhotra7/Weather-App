package com.complete.weatherapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.complete.weatherapplication.Utils.Resources
import com.complete.weatherapplication.Utils.Utils.Companion.REQUEST_CODE
import com.complete.weatherapplication.databinding.ActivityMainBinding
import com.complete.weatherapplication.ui.CurrentFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.location.Criteria
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import java.lang.String


class MainActivity : AppCompatActivity() {

    private var locationManager: LocationManager? = null
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var _binding:ActivityMainBinding? = null
    val binding : ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        installSplashScreen()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getSharedPreferences("shared",Context.MODE_PRIVATE).getString("unit","")?.let {
        } ?: "metric"
        getSharedPreferences("shared",Context.MODE_PRIVATE).getString("name","")?.let {
        } ?: "Username"
       /* getSharedPreferences("shared",Context.MODE_PRIVATE).getString("latitude","0.0")?.let {
        }?:latitude.toString()
        getSharedPreferences("shared",Context.MODE_PRIVATE).getString("longitude","0.0")?.let {
        } ?:longitude.toString()*/

        binding.bottomNavigationView.setupWithNavController(
            supportFragmentManager.findFragmentById(R.id.weatherFragment)!!.findNavController())

    }
   /* @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if(EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient?.lastLocation?.addOnCompleteListener{task ->
                var location:Location? = task.result
                if(location == null){
                }else{
                    Log.d("Debug:" ,"Your Location:"+ location.longitude)
                }
            }

        }else{
            EasyPermissions.requestPermissions(this,"You have to give access to Location!",REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
            Log.d("taget","not have permission")
        }
    }*/


}
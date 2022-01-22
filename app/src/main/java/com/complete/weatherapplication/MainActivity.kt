package com.complete.weatherapplication

import android.Manifest
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
import com.complete.weatherapplication.databinding.ActivityMainBinding
import com.complete.weatherapplication.ui.CurrentFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private var _binding:ActivityMainBinding? = null
    val binding : ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        installSplashScreen()

        getSharedPreferences("shared",Context.MODE_PRIVATE).getString("unit","")?.let {
        } ?: "metric"
        getSharedPreferences("shared",Context.MODE_PRIVATE).getString("name","")?.let {
        } ?: "Username"

        binding.bottomNavigationView.setupWithNavController(
            supportFragmentManager.findFragmentById(R.id.weatherFragment)!!.findNavController())

    }


}
package com.complete.weatherapplication.ui.Activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.complete.weatherapplication.R
import com.complete.weatherapplication.Utils.Utils
import com.complete.weatherapplication.Utils.Utils.Companion.SHARED
import com.complete.weatherapplication.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding : ActivityMainBinding get() = _binding!!
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    private var MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflating the binding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //intialising fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
        //storing in shared prefs
        getSharedPreferences(SHARED,Context.MODE_PRIVATE).getString("name","")?.let {
        } ?: "Username"

        //intialising bottom navigation
        binding.bottomNavigationView.setupWithNavController(
            supportFragmentManager.findFragmentById(R.id.weatherFragment)!!.findNavController()
        )
    }
    //checking the permissions for location
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                /** Alert Dialogue Box to Ask the user for location permission  */
                AlertDialog.Builder(this)
                    .setTitle("Required Location Permission")
                    .setMessage("Grant Permission To Use Current Location, Else default Location will be used")
                    .setPositiveButton("ok") { dialogInterface: DialogInterface?, i: Int ->

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                        )
                        // permission granted
                    }
                    .setNegativeButton("cancel") { dialogInterface: DialogInterface?, i: Int ->
                        // permission not granted
                        Toast.makeText(
                            applicationContext,
                            "Kindly Grant Permission to procceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.create().show()
            } else {
                // request the permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION
                )

            }
        }else{
            getCurrentLocation()
        }
    }
    //getting the longitude and latitude
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permission hasn't been granted
            checkLocationPermission();
        } else {
            //permission granted
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                if(it != null && it.longitude != null && it.latitude != null ){
                    longitude = it.longitude
                    latitude = it.latitude
                    Log.d(Utils.TAG +"2",longitude.toString())
                    Log.d(Utils.TAG +"2",latitude.toString())
                    getSharedPreferences(SHARED, Context.MODE_PRIVATE)?.edit()?.apply{
                        putString("longitude",longitude.toString())
                        putString("latitude",latitude.toString())
                        apply()
                    }
                }else{
                    Log.d(Utils.TAG,"else")
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
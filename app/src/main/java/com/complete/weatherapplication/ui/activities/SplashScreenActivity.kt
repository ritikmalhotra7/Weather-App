package com.complete.weatherapplication.ui.activities

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import coil.load
import com.complete.weatherapplication.databinding.ActivitySplashScreenBinding
import com.complete.weatherapplication.utils.Utils
import com.complete.weatherapplication.utils.Utils.Companion.REQUEST_CODE
import com.complete.weatherapplication.utils.Utils.Companion.TAG
import com.complete.weatherapplication.utils.Utils.Companion.getCurrentLocation
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


class SplashScreenActivity : Activity() {
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    private var _binding: ActivitySplashScreenBinding? = null
    val binding : ActivitySplashScreenBinding
    get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflating the binding
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        EasyPermissions.requestPermissions(this,"hello", REQUEST_CODE,Manifest.permission.ACCESS_FINE_LOCATION);
        if(EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_FINE_LOCATION)){
          /*  longitude = current.longitude
            latitude = current.latitude
            getSharedPreferences(Utils.SHARED, Context.MODE_PRIVATE).edit().apply{
                putString("longitude",longitude.toString())
                putString("latitude",latitude.toString())
                apply()
            }
            Log.d(TAG,current.longitude.toString())*/
        }else{
            Toast.makeText(this,"App didn't get the permissions required",Toast.LENGTH_LONG).show()
        }
        //taking delay to splash screen
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
    /*//checking the permissions for location
    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                *//** Alert Dialogue Box to Ask the user for location permission  *//*
                AlertDialog.Builder(this)
                    .setTitle("Required Location Permission")
                    .setMessage("Grant Permission To Use Current Location, Else default Location will be used")
                    .setPositiveButton("ok") { dialogInterface: DialogInterface?, i: Int ->

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_CODE
                        )
                        // permission granted
                        fusedLocationClient?.lastLocation?.addOnSuccessListener {
                            it.let {
                                longitude = it.longitude
                                latitude = it.latitude
                            }
                        }
                        startActivity(Intent(this, MainActivity::class.java))
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
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )

            }
        }else{
            getCurrentLocation()
        }
    }*/
    //getting the longitude and latitude
   /* private fun getCurrentLocation() {
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
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                if(it != null && it.longitude != null && it.latitude != null ){
                    longitude = it.longitude
                    latitude = it.latitude
                    Log.d(TAG+"1",longitude.toString())
                    Log.d(TAG+"1",latitude.toString())
                }else{
                    Log.d(TAG,"else")
                }
            }
        }
    }*/
    //null the binding

}
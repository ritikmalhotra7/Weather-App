package com.complete.weatherapplication.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.complete.weatherapplication.Utils.CurrentLocation
import com.complete.weatherapplication.ui.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource

class Utils {
    companion object{
        const val BASE_URL = "https://api.openweathermap.org"
        const val API_KEY = "2b50b363f873317f7667e6e0d0b7b7d4"
        const val REQUEST_CODE = 1
        const val SHARED = "shared"
        const val TAG = "taget"

        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }
        /*fun checkLocationPermission(ctx : Activity) :CurrentLocation{
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
            var currentLocation = CurrentLocation(0.0,0.0)
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(ctx, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    *//** Alert Dialogue Box to Ask the user for location permission  *//*
                    *//*AlertDialog.Builder(ctx)
                        .setTitle("Required Location Permission")
                        .setMessage("Grant Permission To Use Current Location, Else default Location will be used")
                        .setPositiveButton("ok") { dialogInterface: DialogInterface?, i: Int ->
                            ActivityCompat.requestPermissions(
                                ctx,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_CODE
                            )
                            // permission granted
                            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                                it.let {
                                    currentLocation =  CurrentLocation(it.longitude,it.latitude)
                                    ctx.getSharedPreferences(SHARED, Context.MODE_PRIVATE).edit().apply{
                                        putString("longitude",currentLocation.longitude.toString())
                                        putString("latitude",currentLocation.latitude.toString())
                                        apply()
                                    }
                                }
                            }
                        }
                        .setNegativeButton("cancel") { dialogInterface: DialogInterface?, i: Int ->
                            // permission not granted
                            Toast.makeText(
                                ctx,
                                "Kindly Grant Permission to procceed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.create().show()*//*
                } else {
                    // request the permission
                    ActivityCompat.requestPermissions(
                        ctx, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                    )

                }
            }else{

                return getCurrentLocation(ctx)
            }
            return currentLocation
        }*/
        @SuppressLint("MissingPermission")
        fun getCurrentLocation(ctx :Activity) : CurrentLocation{
            var current : CurrentLocation = CurrentLocation(0.0,0.0)
            LocationServices.getFusedLocationProviderClient(ctx)
                .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("taget11", it.result.longitude.toString())
                        Log.d("taget12", it.result.latitude.toString())
                        current = CurrentLocation(it.result.longitude,it.result.latitude)
                        ctx.getSharedPreferences(SHARED, Context.MODE_PRIVATE).edit()
                            .putString("longitude",current.longitude.toString())
                            .putString("latitude",current.latitude.toString())
                            .apply()
                    } else {
                        Log.d("taget", "failed")
                    }
                }
            return current
        }
    }
}

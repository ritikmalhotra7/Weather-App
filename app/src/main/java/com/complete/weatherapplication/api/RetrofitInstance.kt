package com.complete.weatherapplication.api

import com.complete.weatherapplication.utils.Utils.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        private val retrofit by lazy{
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()
            Retrofit.Builder().run {
                baseUrl(BASE_URL)
                addConverterFactory(GsonConverterFactory.create())
                client(client)
                build()
            }
        }
        val api by lazy{
            retrofit.create(WeatherApi::class.java)
        }
    }
}
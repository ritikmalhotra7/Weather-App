package com.complete.weatherapplication.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.complete.weatherapplication.R
import com.complete.weatherapplication.databinding.FragmentSearchDateBinding
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.Coil
import coil.load
import com.complete.weatherapplication.Utils.Resources
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.String
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Date
import java.util.*


class SearchDateFragment : Fragment(R.layout.fragment_search_date) {

    private lateinit var  viewModel: WeatherViewModel
    private var _binding: FragmentSearchDateBinding? = null
    val binding : FragmentSearchDateBinding get() = _binding!!
    val latitudeCities = arrayOf(28.7041, 19.0760, 28.5355)
    val longitudeCities = arrayOf(77.1025, 72.8777, 77.3910)
    var spinnerPosition = 0
    var daySelected = ""
    var dateSelected = ""
    private var unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDateBinding.inflate(inflater,container,false)

        binding.username.text = activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.getString("name","")
        binding.calender.setOnDateChangeListener { view1, year, month, dayOfMonth ->
            val str = Month.of(month + 1).toString() + " " + dayOfMonth + " " + year
            Log.d("str",str)
            val df = SimpleDateFormat("MMM dd yyyy")
            // the day selected in the calendar
            daySelected = dayOfMonth.toString() + ""
            Log.d("daySelected",daySelected)
            var date: Date? = null
            try {
                date = df.parse(str)
                Log.d("date",date.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            dateSelected = (date!!.time / 1000L).toString()
            Log.d("dateSelected",dateSelected)
            makeAPICall(latitudeCities[spinnerPosition],longitudeCities[spinnerPosition],dateSelected)
        }
        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        setViews()
        observeCurrent()

        return binding.root
    }
    fun observeCurrent(){
        viewModel.getSearch(longitudeCities[spinnerPosition],latitudeCities[spinnerPosition],unit.toString())
        viewModel.search.observe(viewLifecycleOwner, Observer{response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data?.let{
                        binding.locationName.text = "${it.name},${it.sys.country}"
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
                            binding.temperature.text = "${it.main.temp}°C"
                        }else if(unit == "imperial"){
                            binding.temperature.text = "${it.main.temp}°F"
                        }
                        //binding.visibility.text = "${it.visibility/1000} KM"
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
    fun makeAPICall(latitude: Double, longitude: Double, date: kotlin.String?) {
        val calendarNew = Calendar.getInstance()
        val dNew = calendarNew.time
        val dateFormatNew: DateFormat = SimpleDateFormat("dd")
        val daySelected = daySelected.toInt()
        val dayToday = dateFormatNew.format(dNew).toInt()
        if (daySelected < dayToday) {
            getResultFromAPIPastDays(latitude, longitude, date!!)
        } else {
            getResultFromAPIFutureDays(latitude, longitude, daySelected - dayToday)
        }
    }
    fun setViews() {
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endOfMonth = calendar.timeInMillis
        calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -4)
        val startOfMonth = calendar.timeInMillis
        val c = Calendar.getInstance()
        val d = c.time
        val dateFormat: DateFormat = SimpleDateFormat("dd")
        daySelected = dateFormat.format(d)
        binding.calender.maxDate = endOfMonth
        binding.calender.minDate = startOfMonth
        dateSelected = String.valueOf(binding.calender.date).substring(0, 10)

        binding.calender.setOnDateChangeListener { view1, year, month, dayOfMonth ->
            val str =
                Month.of(month + 1).toString() + " " + dayOfMonth + " " + year
            val df = SimpleDateFormat("MMM dd yyyy")
            // the day selected in the calendar
            daySelected = dayOfMonth.toString() + ""
            var date: Date? = null
            try {
                date = df.parse(str)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            dateSelected = (date!!.time / 1000L).toString()
            val latitude = latitudeCities[spinnerPosition]
            val longitude = longitudeCities[spinnerPosition]
            makeAPICall(latitude, longitude, dateSelected)
        }
        setSpinner()
    }
    fun setSpinner() {
        binding.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                spinnerPosition = position
                val cityName = parentView?.getItemAtPosition(position).toString()
                binding.locationName.text = cityName+", "+"IN"
                makeAPICall(longitudeCities[spinnerPosition],latitudeCities[spinnerPosition],dateSelected)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        })
    }
    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    fun getResultFromAPIFutureDays(latitude: Double, longitude: Double, day: Int) {
        viewModel.getForecast(longitude,latitude,unit.toString())
        viewModel.reportList.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data.let{
                        val dateDif = day
                        val icon = it!!.daily[dateDif].weather[0].icon
                        val dt = it.daily[dateDif].dt.toString()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        binding.condition.text = it.daily[dateDif].weather[0].main
                        binding.ivCondition.load("https://openweathermap.org/img/wn/$icon@4x.png"){
                            crossfade(true)
                        }
                        binding.condition.text = it.daily[dateDif].weather[0].main
                        binding.humidity.text = it.daily[dateDif].humidity.toString()+"%"
                        binding.windspeed.text = "${it.daily[dateDif].wind_speed} Meter/Sec"
                        binding.preasure.text = "${it.daily[dateDif].pressure} hPa"
                        val unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
                        if(unit == "metric"){
                            binding.temperature.text = "(${it.daily[dateDif].temp.max}°C - ${it.daily[dateDif].temp.min}°C)"
                        }else if(unit == "imperial"){
                            binding.temperature.text = "(${it.daily[dateDif].temp.max}°F - ${it.daily[dateDif].temp.min}°F)"
                        }
                        Log.d("taget","${binding.windspeed.text} - windspeed")
                        Log.d("taget",binding.temperature.text.toString())

                    }
                }
                is Resources.Error ->{
                    hideProgressBar()
                    Log.d("taget","error")
                }
                is Resources.Loading ->{
                    showProgressBar()
                }
            }
        })
    }
    fun getResultFromAPIPastDays(latitude: Double, longitude: Double, date: kotlin.String) {
        viewModel.getPastReponse(longitude,latitude,unit.toString(),date.toInt())
        viewModel.pastValues.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data.let{
                        val icon = it!!.current?.weather?.get(0)?.icon.toString()
                        val dt = it.current?.dt.toString()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        binding.condition.text = it.current?.weather?.get(0)?.main
                        binding.ivCondition.load("https://openweathermap.org/img/wn/$icon@4x.png"){
                            crossfade(true)
                        }
                        binding.humidity.text = it.current?.humidity.toString() + "%"
                        binding.windspeed.text = "${it.current?.wind_speed} Meter/Sec"
                        binding.preasure.text = "${it.current?.pressure} hPa"
                        val unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
                        if(unit == "metric"){
                            binding.temperature.text = "${it.current?.temp}°C "
                        }else if(unit == "imperial"){
                            binding.temperature.text = "${it.current?.temp}°F"
                        }
                        Log.d("taget","${binding.windspeed.text} - windspeed")
                        Log.d("taget",binding.temperature.text.toString())

                    }
                }
                is Resources.Error ->{
                    hideProgressBar()
                    Log.d("taget","error")
                }
                is Resources.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

}

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
import androidx.navigation.Navigation
import coil.Coil
import coil.load
import com.complete.weatherapplication.Utils.Resources
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Date
import java.util.*


class SearchDateFragment : Fragment(R.layout.fragment_search_date) {

    private var cityName: String = ""
    private lateinit var  viewModel: WeatherViewModel
    private var _binding: FragmentSearchDateBinding? = null
    val binding : FragmentSearchDateBinding get() = _binding!!
    val latitudeCities = arrayOf(28.7041, 19.0760, 28.5355)
    val longitudeCities = arrayOf(77.1025, 72.8777, 77.3910)
    val cities = arrayOf("New Delhi","Mumbai","Noida")
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
        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        setSpinner()
        setViews()
        makeAPICall(latitudeCities[spinnerPosition],longitudeCities[spinnerPosition],dateSelected)
        binding.locationName.text = cities[spinnerPosition]
        binding.backButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_searchDateFragment_to_currentFragment)
        }


        return binding.root
    }
    fun makeAPICall(latitude: Double, longitude: Double, date: kotlin.String?) {
        val calendarNew = Calendar.getInstance()
        val dNew = calendarNew.time
        val dateFormatNew: DateFormat = SimpleDateFormat("dd")
        val daySelected = daySelected.toInt()
        val dayToday = dateFormatNew.format(dNew).toInt()
        Log.d("taget",date.toString())
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

        /**Reason for limiting the date range for the calendar -
         * Open Weather API only allows the call to be in a
         * specific range under free plan */
        dateSelected = binding.calender.date.toString().substring(0, 10)

        binding.calender.setOnDateChangeListener { view1, year, month, dayOfMonth ->
            val str = Month.of(month + 1).toString() + " " + dayOfMonth + " " + year
            val df = SimpleDateFormat("MMM dd yyyy")
            // the day selected in the calendar
            // the day selected in the calendar
            daySelected = dayOfMonth.toString() + ""

            var date: Date? = null
            try {
                date = df.parse(str)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            dateSelected = (date!!.time / 1000L).toString()
            // the date selected in the calendar
            dateSelected = (date!!.time / 1000L).toString()
            setSpinner()

            Log.d("dateDelected",dateSelected)
            makeAPICall(latitudeCities[spinnerPosition], longitudeCities[spinnerPosition], dateSelected)

        }
        setSpinner()

        makeAPICall(latitudeCities[spinnerPosition], longitudeCities[spinnerPosition], dateSelected)
    }
    fun setSpinner() {
        binding.spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                spinnerPosition = position
                cityName = parentView?.getItemAtPosition(spinnerPosition).toString()
                binding.locationName.text = cityName+", "+"IN"
                setViews()
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
        unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
        Log.d("tagetlongi",longitude.toString())
        Log.d("tagetLati",latitude.toString())
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
                            binding.temperature.text = "(${it.daily[dateDif].temp.max}°C / ${it.daily[dateDif].temp.min}°C)"
                        }else if(unit == "imperial"){
                            binding.temperature.text = "(${it.daily[dateDif].temp.max}°F / ${it.daily[dateDif].temp.min}°F)"
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

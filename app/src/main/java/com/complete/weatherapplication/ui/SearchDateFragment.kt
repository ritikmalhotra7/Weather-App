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
import com.complete.weatherapplication.R
import com.complete.weatherapplication.databinding.FragmentReportBinding
import com.complete.weatherapplication.databinding.FragmentSearchDateBinding
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.complete.weatherapplication.Utils.Resources
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
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
    var spinnerPosition = 1
    var daySelected = ""
    var dateSelected = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchDateBinding.inflate(inflater,container,false)
        binding.username.text = activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.getString("name","")

       /* val customList = listOf("New Delhi","Mumbai","Noida")
        val adapter = ArrayAdapter<String>(requireContext(),R.layout.support_simple_spinner_dropdown_item,customList)
        binding.spinner.adapter = adapter*/
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val cityName = parent?.getItemAtPosition(position).toString()
                Toast.makeText(activity,cityName.toString(),Toast.LENGTH_SHORT).show()
                Log.d("taget",cityName)
                binding.text2.text = cityName
                spinnerPosition = position
                binding.locationName.text = cityName+", "+"IN"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        val unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
        viewModel.getForecast(77.1025,28.7041,unit.toString())/*(longitudeCities[spinnerPosition],latitudeCities[spinnerPosition])*/
        observing()


        val calender = Calendar.getInstance()
        binding.calender.setOnDateChangeListener{view1,year,month,dayofmonth ->

        }
        return binding.root
    }
    fun observing(){
        viewModel.reportList.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data.let{
                        val datediff = 5
                        val icon = it!!.daily[datediff].weather[0].icon
                        val dt = it.daily[datediff].dt.toString()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        binding.condition.text = it.daily[datediff].weather[0].main
                        Glide.with(this).load("https://openweathermap.org/img/wn/$icon@4x.png").into(binding.ivCondition)
                        binding.condition.text = it.daily[datediff].weather[0].main
                        binding.humidity.text = it.daily[datediff].humidity.toString()+"%"
                        binding.windspeed.text = "${it.daily[datediff].wind_speed} Meter/Sec"
                        binding.preasure.text = "${it.daily[datediff].pressure} hPa"
                        binding.temperature.text = "(${it.daily[datediff].temp.max}°C - ${it.daily[datediff].temp.min}°C)"
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
    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }


}
/* val datediff = 0
                        val icon = it!!.daily[datediff].weather[0].icon
                        val dt = it.daily[datediff].dt.toString()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        val date  = Date(dt.toLong() * 1000)
                        sdf.format(date)
                        binding.text2.text = date.toString()
                        binding.condition.text = it.daily[datediff].weather[0].main
                        Glide.with(this).load("https://openweathermap.org/img/wn/$icon@4x.png").into(binding.ivCondition)
                        binding.condition.text = it.daily[datediff].weather[0].main
                        binding.humidity.text = it.daily[datediff].humidity.toString()+"%"
                        binding.windspeed.text = "${it.daily[datediff].wind_speed} Meter/Sec"
                        binding.preasure.text = "${it.daily[datediff].pressure} hPa"
                        binding.temperature.text = "(${it.daily[datediff].temp.max}°C - ${it.daily[datediff].temp.min}°C)"
                        Log.d("taget","${binding.windspeed.text} - windspeed")
                        Log.d("taget",binding.temperature.text.toString())*/
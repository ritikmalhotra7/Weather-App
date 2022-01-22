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
    private var unit:kotlin.String? = null

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
                spinnerPosition = position
                binding.locationName.text = cityName+", "+"IN"
                viewModel.getSearch(longitudeCities[spinnerPosition],latitudeCities[spinnerPosition],unit.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        setViews()
        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
        viewModel.getSearch(longitudeCities[spinnerPosition],latitudeCities[spinnerPosition],unit.toString())
        observeCurrent()
        return binding.root
    }
    fun observeCurrent(){
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
    fun observeForecast(dateDiff:Int){
        viewModel.reportList.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data.let{
                        val dateDif = dateDiff
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
    fun observingPast(){
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
    private fun hideProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
    }
    @RequiresApi(Build.VERSION_CODES.O)
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

        binding.calender.setOnDateChangeListener { view1, year, month, dayOfMonth ->
            setViews()
            val str =
                Month.of(month + 1).toString() + " " + dayOfMonth + " " + year
            val df = SimpleDateFormat("MM dd yyyy")
            daySelected = dayOfMonth.toString() + ""
            var date: Date? = null
            try {
                date = df.parse(str)
                Log.d("tagetdate", date.toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            dateSelected = (date!!.time / 1000L).toString()
            dateSelected = String.valueOf(binding.calender.date).substring(0, 10)
            val calendarNew = Calendar.getInstance()
            val daySelects = daySelected.toInt()
            val dayToday = calendarNew.get(Calendar.DAY_OF_YEAR)


            /*val dNew = calendarNew.time
            val dateFormatNew: DateFormat = SimpleDateFormat("dd")
            val daySelects = daySelected.toInt()
            val dayToday = dateFormatNew.format(dNew).toInt()*/
            if (daySelects < dayToday) {
                Log.d("taget",daySelected.toString())
                Log.d("taget",daySelects.toString())
                viewModel.getPastReponse(longitudeCities[spinnerPosition], latitudeCities[spinnerPosition], unit.toString(), daySelects!!.toInt())
                observingPast()

            } else {
                // the date selected is current date or ahead
                viewModel.getForecast(
                    longitudeCities[spinnerPosition],
                    latitudeCities[spinnerPosition],
                    unit.toString()
                )
                observeForecast(daySelects - dayToday)
            }
        }
        }
}

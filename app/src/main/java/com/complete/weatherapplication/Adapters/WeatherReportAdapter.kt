package com.complete.weatherapplication.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.databinding.ReportListBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherReportAdapter(var list:ArrayList<Daily>,public val ctx:Context): RecyclerView.Adapter<WeatherReportAdapter.AdapterViewHolder>() {
    private fun valueFromKey(key: String): String? {
        return this.ctx.getSharedPreferences("shared",Context.MODE_PRIVATE)!!.getString(key,"")
    }
    class AdapterViewHolder(val binding:ReportListBinding) :RecyclerView.ViewHolder(binding.root){
    }
       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(ReportListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val daily = list[position]
        holder.binding.apply {
            /*val dt = daily.dt.toString()
            date.text = dt*/
            val dt = daily.dt.toString()
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val dat  = Date(dt.toLong() * 1000)
            sdf.format(dat)
            date.text = dat.toString()
            humidity.text = "Humidity - ${daily.humidity}%"
            preasure.text = "Preasure - ${daily.pressure}hPa"
            windspeed.text = "Wind Speed - ${daily.wind_speed} M/Sec"
            conditioninlocation.text = "${daily.weather[0].main}"
            val unit = valueFromKey("unit")
            if(unit == "metric"){
                temperature.text = "Temp(Max-Min) - ${daily.temp.max}°C - ${daily.temp.min}°C"
            }else if(unit == "imperial"){
                temperature.text = "Temp(Max-Min) - ${daily.temp.max}°F - ${daily.temp.min}°F"
            }
            description.text = "${daily.weather[0].description}"
            ivCondition.load("https://openweathermap.org/img/wn/${daily.weather[0].icon}@4x.png"){
                crossfade(true)
            }
        }


    }
    fun setData(newList:ArrayList<Daily>){
        val diffUtil = WeatherDiffUtils(list,newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        list = newList
        diffResults.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
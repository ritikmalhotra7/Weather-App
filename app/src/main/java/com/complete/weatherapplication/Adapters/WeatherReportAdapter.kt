package com.complete.weatherapplication.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.complete.weatherapplication.Model.Main
import com.complete.weatherapplication.Model.Weather
import com.complete.weatherapplication.Model.WeatherResponse
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.databinding.ReportListBinding

class WeatherReportAdapter(var list:List<Daily>): RecyclerView.Adapter<WeatherReportAdapter.AdapterViewHolder>() {
    class AdapterViewHolder(val binding:ReportListBinding) :RecyclerView.ViewHolder(binding.root){

    }
       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(ReportListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val daily = list[position]
        holder.binding.apply {
            humidity.text = "${daily.humidity}%"
            preasure.text = "${daily.pressure}hPa"
            temperature.text = "${daily.temp.max}-${daily.temp.min}"
            windspeed.text = "${(daily.wind_speed * 60)/1000}km/sec"
        }


    }
    fun setData(newList:List<Daily>){
        val diffUtil = WeatherDiffUtils(list,newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        list = newList
        diffResults.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
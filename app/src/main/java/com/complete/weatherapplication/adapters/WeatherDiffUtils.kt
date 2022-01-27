package com.complete.weatherapplication.adapters

import androidx.recyclerview.widget.DiffUtil
import com.complete.weatherapplication.model2.Daily

class WeatherDiffUtils(
    private val oldList : List<Daily>,
    private val newList : List<Daily>
):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
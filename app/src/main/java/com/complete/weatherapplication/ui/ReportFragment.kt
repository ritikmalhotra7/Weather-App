package com.complete.weatherapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.weatherapplication.Adapters.WeatherReportAdapter
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.R
import com.complete.weatherapplication.Utils.Resources
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
import com.complete.weatherapplication.databinding.FragmentCurrentBinding
import com.complete.weatherapplication.databinding.FragmentReportBinding

class ReportFragment : Fragment(R.layout.fragment_report) {

    private lateinit var rvAdapter:WeatherReportAdapter
    private lateinit var list:ArrayList<Daily>

    private var _binding: FragmentReportBinding? = null
    val binding : FragmentReportBinding get() = _binding!!

    val args:ReportFragmentArgs by navArgs()

    private lateinit var viewModel:WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater,container,false)
        list = ArrayList()
        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        viewModel.getForecast(77.142873,28.664934)
        observing()
        binding.forlocation.text = args.cityName
        setupRecView(list)

        return binding.root
    }
    private fun hideProgressBar(){
        binding.progressbar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        binding.progressbar.visibility = View.VISIBLE
    }
    fun observing(){
        viewModel.reportList.observe(viewLifecycleOwner,Observer{response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data.let{
                        for(i in it!!.daily){
                            list.add(i)
                            Log.d("taget",i.toString())
                        }
                        setupRecView(list)
                        Log.d("taget","success")

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
    fun setupRecView(listy:ArrayList<Daily>){
        rvAdapter = WeatherReportAdapter(listy)
        binding.rv.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            rvAdapter.setData(listy)
        }

    }
}
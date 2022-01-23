package com.complete.weatherapplication.ui.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.weatherapplication.Adapters.WeatherReportAdapter
import com.complete.weatherapplication.Model2.Daily
import com.complete.weatherapplication.R
import com.complete.weatherapplication.Utils.Resources
import com.complete.weatherapplication.Utils.Utils
import com.complete.weatherapplication.Utils.Utils.Companion.SHARED
import com.complete.weatherapplication.Utils.Utils.Companion.TAG
import com.complete.weatherapplication.ui.WeatherRepository
import com.complete.weatherapplication.ui.WeatherViewModel
import com.complete.weatherapplication.ui.WeatherViewmodelFactory
import com.complete.weatherapplication.databinding.FragmentReportBinding

class ReportFragment : Fragment(R.layout.fragment_report) {

    private lateinit var rvAdapter:WeatherReportAdapter
    private lateinit var list:ArrayList<Daily>

    private var _binding: FragmentReportBinding? = null
    val binding : FragmentReportBinding get() = _binding!!

    var latitude : Double = 0.0
    var longitude : Double = 0.0

    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater,container,false)
        list = ArrayList()
        latitude= activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("latitude","")?.toDouble()?:0.0
        longitude = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("longitude","")?.toDouble()?:0.0
        binding.username.text = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("name","")
        val unit = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("unit","metric")
        binding.forlocation.text = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("cityname","")

        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        if(Utils.isOnline(requireActivity())){
            viewModel.getForecast(longitude,latitude,unit.toString())
            observing()
        }else{
            Toast.makeText(activity,"No Internet Connection!", Toast.LENGTH_SHORT).show()
        }
        binding.backButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_reportfragment_to_currentFragment)
        }

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
                        }
                        setupRecView(list)
                    }
                }
                is Resources.Error ->{
                    hideProgressBar()
                    Log.d(TAG,"error")
                }
                is Resources.Loading ->{
                    showProgressBar()
                }
            }
        })
    }
    fun setupRecView(listy:ArrayList<Daily>){
        rvAdapter = WeatherReportAdapter(listy,requireContext())
        binding.rv.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            rvAdapter.setData(listy)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
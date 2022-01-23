package com.complete.weatherapplication.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.BlurTransformation
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

    var latitude : Double = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("latitude","0.0")?.toDouble()?:0.0
    var longitude : Double = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("longitude","0.0")?.toDouble()?:0.0

    private lateinit var viewModel:WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater,container,false)
        list = ArrayList()
        binding.username.text = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("cityname","")
        val repo = WeatherRepository()
        val factory = WeatherViewmodelFactory(repo)
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        val unit = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("unit","metric")
        viewModel.getForecast(longitude,latitude,unit.toString())
        observing()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        binding.forlocation.text = sharedPref!!.getString("cityName","")
        setupRecView(list)
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
                    Log.d("taget","error")
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
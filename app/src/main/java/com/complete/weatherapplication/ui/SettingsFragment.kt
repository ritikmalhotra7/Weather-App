package com.complete.weatherapplication.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.complete.weatherapplication.R
import com.complete.weatherapplication.WeatherRepository
import com.complete.weatherapplication.WeatherViewModel
import com.complete.weatherapplication.WeatherViewmodelFactory
import com.complete.weatherapplication.databinding.FragmentReportBinding
import com.complete.weatherapplication.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    val binding : FragmentSettingsBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)

        binding.edit.setOnClickListener{
            binding.edityourname.visibility = View.VISIBLE
            binding.tick.visibility = View.VISIBLE
            binding.helloname.visibility = View.INVISIBLE
            binding.edit.visibility = View.GONE
        }
        binding.tick.setOnClickListener{

            binding.helloname.text = binding.edityourname.text
            val sh = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)
            sh!!.edit().apply(){
                putString("name",binding.edityourname.text.toString())
                apply()
            }
            binding.edityourname.visibility = View.GONE
            binding.edit.visibility = View.VISIBLE
            binding.helloname.visibility = View.VISIBLE
            binding.tick.visibility = View.GONE
        }
        val name = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)?.getString("name","username")
        binding.helloname.text = name

        binding.c.setOnClickListener {
            val sh = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)
            sh!!.edit().apply(){
                putString("unit","metric")
                apply()
            }
            Toast.makeText(activity,"unit->°C",Toast.LENGTH_SHORT).show()
        }
        binding.f.setOnClickListener {
            val sh = activity?.getSharedPreferences("shared",Context.MODE_PRIVATE)
            sh!!.edit().apply(){
                putString("unit","imperial")
                apply()
            }
            Toast.makeText(activity,"unit->°F",Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }
}
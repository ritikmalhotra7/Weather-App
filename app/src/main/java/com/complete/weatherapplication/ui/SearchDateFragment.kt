package com.complete.weatherapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.complete.weatherapplication.R
import com.complete.weatherapplication.databinding.FragmentReportBinding
import com.complete.weatherapplication.databinding.FragmentSearchDateBinding

class SearchDateFragment : Fragment(R.layout.fragment_search_date) {

    private var _binding: FragmentSearchDateBinding? = null
    val binding : FragmentSearchDateBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDateBinding.inflate(inflater,container,false)
        var cities = arrayListOf<String>("New Delhi","Mumbai","Noida")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

}
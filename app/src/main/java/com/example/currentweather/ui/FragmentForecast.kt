package com.example.currentweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.currentweather.R
import com.example.currentweather.models.WeatherResponse

class FragmentForecast : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun updateView(weather: WeatherResponse) {
        super.updateView(weather)
    }
}
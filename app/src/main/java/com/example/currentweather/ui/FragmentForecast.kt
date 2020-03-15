package com.example.currentweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.currentweather.R
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.util.Logger
import com.example.currentweather.util.convertSecondToString

class FragmentForecast : BaseFragment<ForecastResponse>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun updateView(weather: ForecastResponse) {
        Logger.log(
            "FragmentForecast",
            "updateView, city: ${weather.cityName}, date: ${weather.forecast[0].date.convertSecondToString()}"
        )
        super.updateView(weather)
    }
}
package com.example.currentweather.ui

import androidx.fragment.app.Fragment
import com.example.currentweather.models.WeatherResponse

open class BaseFragment : Fragment() {
    open fun updateView(weather: WeatherResponse) {
    }
}
package com.example.currentweather.ui

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.currentweather.models.BaseResponse

open class BaseFragment<in T : BaseResponse> : Fragment() {

    open fun requestUpdate(cityName: String?) {}

    open fun changeCity(newCityName: String) {}

    open fun observe(cityName: String?, textView: TextView) {}

    open fun updateView(weather: T) {}

}
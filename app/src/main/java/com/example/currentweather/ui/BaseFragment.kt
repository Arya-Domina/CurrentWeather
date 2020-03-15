package com.example.currentweather.ui

import androidx.fragment.app.Fragment
import com.example.currentweather.models.BaseResponse

open class BaseFragment<in T : BaseResponse> : Fragment() {
    open fun updateView(weather: T) {
    }
}
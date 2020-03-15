package com.example.currentweather.models

import androidx.annotation.StringRes

class WeatherException(exception: Throwable, @StringRes val stringRes: Int) : Throwable(exception) {
}
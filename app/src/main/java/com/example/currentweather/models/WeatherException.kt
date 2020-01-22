package com.example.currentweather.models

class WeatherException(exception: Throwable, val stringRes: Int) : Throwable(exception) {
}
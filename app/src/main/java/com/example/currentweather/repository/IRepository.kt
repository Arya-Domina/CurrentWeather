package com.example.currentweather.repository

import com.example.currentweather.models.Parameter
import com.example.currentweather.models.WeatherResponse
import io.reactivex.Single

interface IRepository {
    fun getWeather(param: Parameter? = null): Single<WeatherResponse>
}
package com.example.currentweather.repository

import com.example.currentweather.models.Params
import com.example.currentweather.models.WeatherResponse
import io.reactivex.Single

interface IRepository {
    fun getWeather(param: Pair<Params, Any>? = null): Single<WeatherResponse>
}
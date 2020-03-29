package com.example.currentweather.repository

import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.models.Params
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.PreferenceHelper
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

class LocalRepository : IRepository, KoinComponent {
    private val preferenceHelper: PreferenceHelper by inject()

    override fun getWeather(param: Pair<Params, Any>?): Single<WeatherResponse> {
        return Single.just(preferenceHelper.getWeather())
    }

    fun getForecast(): Single<ForecastResponse> {
        return Single.just(preferenceHelper.getForecast())
    }
}
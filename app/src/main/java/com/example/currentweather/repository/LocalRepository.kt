package com.example.currentweather.repository

import com.example.currentweather.models.Parameter
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.PreferenceHelper
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

class LocalRepository : IRepository, KoinComponent {
    private val preferenceHelper: PreferenceHelper by inject()

    override fun getWeather(param: Parameter?): Single<WeatherResponse> {
        return Single.just(preferenceHelper.getWeather())
    }
}
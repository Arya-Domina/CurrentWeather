package com.example.currentweather.repository

import com.example.currentweather.Constants.Companion.INTERVAL
import com.example.currentweather.models.Coordination
import com.example.currentweather.models.Params
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class WeatherRepository : KoinComponent {

    private val networkRepo: NetworkRepository by inject()
    private val localRepo: LocalRepository by inject()
    private val preferenceHelper: PreferenceHelper by inject()

    fun getCurrentWeather(param: Pair<Params, Any?>): Single<WeatherResponse> {
        return getLocalWeather()
            .flatMap {
                val second = if (param.second == null) {
                    it.getSavedParameter(param.first)
                } else {
                    param.second!!
                }
                val p: Pair<Params, Any> = Pair(param.first, second)
                if (isParamMath(p, it) && !isTimePassed(it.date))
                    Single.just(it)
                else getNetworkWeather(p)
            }
            .doOnSuccess {
                preferenceHelper.saveWeather(it)
            }
            .onErrorReturn { WeatherResponse() }
    }

    private fun WeatherResponse.getSavedParameter(params: Params): Any {
        return when (params) {
            Params.CityName -> cityName ?: ""
            Params.CityId -> id ?: 0
            Params.CityCoord -> coordination ?: Coordination(0.0, 0.0)
        }
    }

    private fun isParamMath(param: Pair<Params, Any>, r: WeatherResponse): Boolean {
        return when (param.first) {
            Params.CityName -> r.cityName == param.second
            Params.CityId -> r.id == param.second
            Params.CityCoord -> r.coordination == param.second
        }
    }

    private fun isTimePassed(date: Long?): Boolean {
        return (Date().time / 1000) - (date ?: 0) > INTERVAL
    }

    private fun getLocalWeather(): Single<WeatherResponse> {
        Logger.log("WeatherRepository", "getLocalWeather")
        return localRepo.getWeather()
    }

    private fun getNetworkWeather(param: Pair<Params, Any>): Single<WeatherResponse> {
        Logger.log("WeatherRepository", "getNetworkWeather")
        return networkRepo.getWeather(param)
    }

}
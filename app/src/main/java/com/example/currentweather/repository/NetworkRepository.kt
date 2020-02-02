package com.example.currentweather.repository

import com.example.currentweather.models.Coordination
import com.example.currentweather.models.Parameter
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.rest.ApiService
import com.example.currentweather.util.Logger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class NetworkRepository : IRepository, KoinComponent {

    private val apiService: ApiService by inject()

    override fun getWeather(param: Parameter?): Single<WeatherResponse> {
        val language = Locale.getDefault().language
        return try {
            when (param) {
                is Parameter.City -> getCurrentWeather(param.cityName, language)
                is Parameter.Id -> getCurrentWeather(param.id, language)
                is Parameter.Coord -> getCurrentWeather(param.coordination, language)
                null -> Single.error<WeatherResponse>(Throwable("No parameters exception")) // TODO WeatherException
            }
        } catch (e: Exception) {
            Logger.log("NetworkRepository", "getWeather: err", e)
            Single.error<WeatherResponse>(e)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getCurrentWeather(cityName: String, language: String): Single<WeatherResponse> {
        return apiService.getCurrentWeather(cityName, language)
    }

    private fun getCurrentWeather(cityId: Long, language: String): Single<WeatherResponse> {
        return apiService.getCurrentWeather(cityId, language)
    }

    private fun getCurrentWeather(coord: Coordination, language: String): Single<WeatherResponse> {
        return apiService.getCurrentWeather(coord.latitude, coord.longitude, language)
    }

}
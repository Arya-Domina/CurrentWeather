package com.example.currentweather.repository

import com.example.currentweather.models.Coordination
import com.example.currentweather.models.Params
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

    override fun getWeather(param: Pair<Params, Any>?): Single<WeatherResponse> {
        val language = Locale.getDefault().language
        return try {
            when (param?.first) {
                Params.CityName -> getCurrentWeather(param.second as String, language)
                Params.CityId -> getCurrentWeather(param.second as Long, language)
                Params.CityCoord -> getCurrentWeather(param.second as Coordination, language)
                else -> Single.error<WeatherResponse>(Throwable("No parameters exception"))
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
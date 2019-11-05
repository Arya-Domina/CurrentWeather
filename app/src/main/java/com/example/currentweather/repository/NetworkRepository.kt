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

class NetworkRepository : IRepository, KoinComponent {

    private val apiService: ApiService by inject()

    override fun getWeather(param: Pair<Params, Any>?): Single<WeatherResponse> {
        return try {
            when (param?.first) {
                Params.CityName -> getCurrentWeather(param.second as String)
                Params.CityId -> getCurrentWeather(param.second as Long)
                Params.CityCoord -> getCurrentWeather(param.second as Coordination)
                else -> Single.error<WeatherResponse>(Throwable("No parameters exception"))
            }
        } catch (e: Exception) {
            Logger.log("NetworkRepository", "getWeather: err")
            Single.error<WeatherResponse>(e)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getCurrentWeather(cityName: String): Single<WeatherResponse> {
        return apiService.getCurrentWeather(cityName)
    }

    private fun getCurrentWeather(cityId: Long): Single<WeatherResponse> {
        return apiService.getCurrentWeather(cityId)
    }

    private fun getCurrentWeather(coord: Coordination): Single<WeatherResponse> {
        return apiService.getCurrentWeather(coord.latitude, coord.longitude)
    }

}
package com.example.currentweather.repository

import com.example.currentweather.Constants.Companion.DEFAULT_CITY
import com.example.currentweather.Constants.Companion.INTERVAL
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.models.Parameter
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import io.reactivex.Observable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class WeatherRepository : KoinComponent {

    private val networkRepo: NetworkRepository by inject()
    private val localRepo: LocalRepository by inject()
    private val preferenceHelper: PreferenceHelper by inject()

    fun getCurrentWeather(param: Parameter? = null): Observable<WeatherResponse> {

        return Observable.create { emitter ->
            Logger.log("WeatherRepository", "obs create")

            getLocalWeather().subscribe({ response ->
                Logger.log("WeatherRepository", "obs getLocalWeather")

                val p = param ?: Parameter.Id(response.id ?: 0)
                Logger.log("WeatherRepository", "p: $p")

                emitter.onNext(response)

                if (!isParamMath(p, response) || isTimePassed(response.date)) {
                    getNetworkWeather(p).subscribe({ secondResponse ->
                        Logger.log("WeatherRepository", "obs getNetworkWeather")
                        preferenceHelper.saveWeather(secondResponse)
                        emitter.onNext(secondResponse)
                        emitter.onComplete()
                    }, { throwable ->
                        emitter.onError(throwable)
                    })
                } else {
                    emitter.onComplete()
                }
            }, {})
        }
    }

    fun getForecast(cityName: String?): Single<ForecastResponse> {
        Logger.log("WeatherRepository", "getForecast $cityName")

        return localRepo.getForecast().flatMap {
            val city = cityName ?: it.cityName ?: DEFAULT_CITY
            if (it.forecast.isNotEmpty().also { Logger.log("WeatherRepository", "isNotEmpty $it") }
                && (it.cityName == cityName || cityName == null).also { Logger.log("WeatherRepository", "equals names $it") }
                && (!isTimePassed(it.forecast[0].date)).also { Logger.log("WeatherRepository", "!time $it") } ) {
                Logger.log("WeatherRepository", "forecast local ${it.cityName}")
                Single.just(it)
            } else {
                Logger.log("WeatherRepository", "forecast network $city")
                networkRepo.getForecast(city)
            }
        }
            .doOnSuccess {
                preferenceHelper.saveForecast(it)
            }
    }

    private fun isParamMath(param: Parameter, r: WeatherResponse): Boolean {
        return when (param) {
            is Parameter.City -> r.cityName == param.cityName
            is Parameter.Id -> r.id == param.id
            is Parameter.Coord -> r.coordination == param.coordination
        }
    }

    private fun isTimePassed(date: Long?): Boolean {
        return ((Date().time / 1000) - (date ?: 0) > INTERVAL).also { Logger.log("WeatherRepository", "isTimePassed $it") }
    }

    private fun getLocalWeather(): Single<WeatherResponse> {
        Logger.log("WeatherRepository", "getLocalWeather")
        return localRepo.getWeather()
    }

    private fun getNetworkWeather(param: Parameter): Single<WeatherResponse> {
        Logger.log("WeatherRepository", "getNetworkWeather")
        return networkRepo.getWeather(param)
    }

}
package com.example.currentweather.repository

import com.example.currentweather.Constants.Companion.DEFAULT_CITY
import com.example.currentweather.Constants.Companion.INTERVAL
import com.example.currentweather.R
import com.example.currentweather.models.*
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import io.reactivex.Observable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class WeatherRepository : KoinComponent {

    private val networkRepo: NetworkRepository by inject()
    private val localRepo: LocalRepository by inject()
    private val preferenceHelper: PreferenceHelper by inject()

    fun getCurrentWeather(param: Pair<Params, Any?>): Observable<WeatherResponse> {

        return Observable.create { emitter ->
            Logger.log("WeatherRepository", "obs create")

            getLocalWeather().subscribe({ response ->
                Logger.log("WeatherRepository", "obs getLocalWeather")

                val second = param.second?.let { it } ?: response.getSavedParameter(param.first)
                val p: Pair<Params, Any> = Pair(param.first, second)
                Logger.log("WeatherRepository", "p: (${p.first}, ${p.second})")
                emitter.onNext(response)

                if (!isParamMath(p, response) || isTimePassed(response.date)) {
                    getNetworkWeather(p).subscribe({ secondResponse ->
                        Logger.log("WeatherRepository", "obs getNetworkWeather")
                        preferenceHelper.saveWeather(secondResponse)
                        emitter.onNext(secondResponse)
                        emitter.onComplete()
                    }, { throwable ->
                        val th =
                            when (throwable) {
                                is IOException -> WeatherException(throwable, R.string.err_no_connection)
                                // UnknownHostException || SocketTimeoutException // timeout || NoRouteToHostException // No route to host
                                is HttpException -> WeatherException(throwable, R.string.err_wrong_city_name) // && param.first == Params.CityName
                                else -> WeatherException(throwable, R.string.err_unknown)
                            }
                        emitter.onError(th)
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
                Logger.log("WeatherRepository", "forecast network $cityName")
                networkRepo.getForecast(city)
            }
        }
            .doOnSuccess {
                preferenceHelper.saveForecast(it)
            }
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
        return ((Date().time / 1000) - (date ?: 0) > INTERVAL).also { Logger.log("WeatherRepository", "isTimePassed $it") }
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
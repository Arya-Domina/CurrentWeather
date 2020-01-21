package com.example.currentweather.repository

import com.example.currentweather.Constants.Companion.INTERVAL
import com.example.currentweather.R
import com.example.currentweather.models.Coordination
import com.example.currentweather.models.Params
import com.example.currentweather.models.WeatherException
import com.example.currentweather.models.WeatherResponse
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

                val second = if (param.second == null) {
                    response.getSavedParameter(param.first)
                } else {
                    param.second!!
                }
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
                                is IOException -> WeatherException(R.string.err_no_connection)
                                // UnknownHostException || SocketTimeoutException // timeout || NoRouteToHostException // No route to host
                                is HttpException -> WeatherException(R.string.err_wrong_city_name) // && param.first == Params.CityName
                                else -> WeatherException(R.string.err_unknown)
                            }
                        emitter.onError(th)
                    })
                } else {
                    emitter.onComplete()
                }
            }, {})
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
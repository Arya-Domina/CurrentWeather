package com.example.currentweather.rest

import com.example.currentweather.Constants
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.models.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("lang") lang: String = "ru",
        @Query("appid") appid: String = Constants.APP_ID_KEY
    ): Single<WeatherResponse>

    @GET("data/2.5/forecast")
    fun getWeatherForecast(
        @Query("q") cityName: String,
        @Query("lang") lang: String = "ru",
        @Query("appid") appid: String = Constants.APP_ID_KEY
    ): Single<ForecastResponse>

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("id") id: Long,
        @Query("lang") lang: String = "ru",
        @Query("appid") appid: String = Constants.APP_ID_KEY
    ): Single<WeatherResponse>

    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String = "ru",
        @Query("appid") appid: String = Constants.APP_ID_KEY
    ): Single<WeatherResponse>

}
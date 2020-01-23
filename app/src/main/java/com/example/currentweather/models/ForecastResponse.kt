package com.example.currentweather.models

import com.example.currentweather.util.convertSecondToString

class ForecastResponse(
    val cityName: String? = null,
    val countryCode: String? = null,
    val timeZone: Long? = null,
    val coordination: Coordination? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val forecast: List<Forecast> = listOf()
) {

    override fun toString(): String {
        return "ForecastResponse(cityName=$cityName, " +
                "countryCode=$countryCode, " +
                "timeZone=$timeZone, " +
                "coordination=$coordination, " +
                "sunrise=${sunrise.convertSecondToString()}, " +
                "sunset=${sunset.convertSecondToString()}, " +
                "Forecast=${forecast.toString()})"
    }
}
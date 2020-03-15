package com.example.currentweather.models

import com.example.currentweather.util.convertSecondToString

class ForecastItem(
    val date: Long? = null,
    val temperature: Double? = null,
    val temperatureFeels: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val weatherMain: String? = null,
    val weatherDescription: String? = null,
    val clouds: Int? = null,
    val windSpeed: Double? = null,
    val windDegree: Int? = null,
    val dateTxt: String? = null
) {

    override fun toString(): String {
        return "(date=${date.convertSecondToString()}, " +
                "temp=$temperature, " +
                "feels=$temperatureFeels, " +
                "pres=$pressure, " +
                "humi=$humidity, " +
                "main=$weatherMain, " +
                "descr=$weatherDescription, " +
                "clouds=$clouds, " +
                "speed=$windSpeed, " +
                "deg=$windDegree, " +
                "dt_txt=$dateTxt)"
    }
}
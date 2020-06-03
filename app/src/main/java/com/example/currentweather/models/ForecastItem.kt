package com.example.currentweather.models

import com.example.currentweather.util.convertKtoC
import com.example.currentweather.util.convertSecondToString
import com.example.currentweather.util.isMidnight

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
    val rain: Double? = null,
    val snow: Double? = null,
    val dateTxt: String? = null
) {

    override fun toString(): String {
        return "\n(date=${date.convertSecondToString()}, " +
                "temp=%.2f, ".format(temperature?.convertKtoC()) +
//                "date=$date, " +
//                "feels=$temperatureFeels, " +
//                "pres=$pressure, " +
//                "humi=$humidity, " +
//                "main=$weatherMain, " +
//                "descr=$weatherDescription, " +
//                "clouds=$clouds, " +
//                "speed=$windSpeed, " +
//                "deg=$windDegree, " +
                "rain=$rain, " +
                "snow=$snow, " +
//                "$dateTxt" +
                ")" + if (date.isMidnight()) " midnight" else ""
//        ")"
    }
}
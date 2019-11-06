package com.example.currentweather.models

import com.example.currentweather.Constants.Companion.DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class WeatherResponse(
    var coordination: Coordination? = null,
    var weatherMain: String? = null,
    var weatherDescription: String? = null,
    var temperature: Double? = null,
    var pressure: Int? = null,
    var humidity: Int? = null,
    var visibility: Int? = null,
    var windSpeed: Double? = null,
    var windDegree: Int? = null,
    var clouds: Int? = null,
    var date: Long? = null,
    var countryCode: String? = null,
    var sunrise: Long? = null,
    var sunset: Long? = null,
    var timeZone: Long? = null,
    var id: Long? = null,
    var cityName: String? = null
) {

    override fun toString(): String {
        return "WeatherResponse(" +
                "coord=$coordination, " +
                "main=$weatherMain, " +
                "description=${weatherDescription}, " +
                "temperature=$temperature, " +
                "pressure=$pressure, " +
                "humidity=$humidity, " +
                "visibility=$visibility, " +
                "speed=$windSpeed, " +
                "degree=$windDegree, " +
                "clouds=$clouds, " +
                "date=${date.convertToString()}, " +
                "country=$countryCode, " +
                "sunrise=${sunrise.convertToString()}, " +
                "sunset=${sunset.convertToString()}, " +
                "timeZone=$timeZone, " +
                "id=$id, " +
                "cityName=$cityName)"
    }

    private fun Long?.convertToString(): String? {
        return this?.let {
            SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(this.convertStoMS())
        }
    }

    private fun Long.convertStoMS(): Long = this * 1000

}
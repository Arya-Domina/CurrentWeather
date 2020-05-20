package com.example.currentweather.models

import com.example.currentweather.util.convertSecondToString

class WeatherResponse(
    cityName: String? = null,
    countryCode: String? = null,
    coordination: Coordination? = null,
    sunrise: Long? = null,
    sunset: Long? = null,
    timeZone: Long? = null,
    val weatherMain: String? = null,
    val weatherDescription: String? = null,
    val temperature: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val visibility: Int? = null,
    val windSpeed: Double? = null,
    val windDegree: Int? = null,
    val clouds: Int? = null,
    val date: Long? = null,
    val id: Long? = null
) : BaseResponse(cityName, countryCode, coordination, sunrise, sunset, timeZone) {

    override fun toString(): String {
        return "WeatherResponse(" +
                super.toString() +
                "main=$weatherMain, " +
                "description=$weatherDescription, " +
                "temperature=$temperature, " +
                "pressure=$pressure, " +
                "humidity=$humidity, " +
                "visibility=$visibility, " +
                "speed=$windSpeed, " +
                "degree=$windDegree, " +
                "clouds=$clouds, " +
                "date=${date.convertSecondToString()}, " +
                "id=$id)"
    }
}
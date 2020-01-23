package com.example.currentweather.models

import com.example.currentweather.util.convertSecondToString

class WeatherResponse(
    val coordination: Coordination? = null,
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
    val countryCode: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val timeZone: Long? = null,
    val id: Long? = null,
    val cityName: String? = null
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
                "date=${date.convertSecondToString()}, " +
                "country=$countryCode, " +
                "sunrise=${sunrise.convertSecondToString()}, " +
                "sunset=${sunset.convertSecondToString()}, " +
                "timeZone=$timeZone, " +
                "id=$id, " +
                "cityName=$cityName)"
    }
}
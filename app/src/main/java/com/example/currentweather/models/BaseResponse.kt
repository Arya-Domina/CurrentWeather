package com.example.currentweather.models

import com.example.currentweather.util.convertSecondToString

open class BaseResponse(
    val cityName: String? = null,
    val countryCode: String? = null,
    val coordination: Coordination? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val timeZone: Long? = null
) {
    override fun toString(): String {
        return "(cityName=$cityName, " +
                "countryCode=$countryCode, " +
                "coordination=$coordination, " +
                "sunrise=${sunrise.convertSecondToString()}, " +
                "sunset=${sunset.convertSecondToString()}, " +
                "timeZone=$timeZone)"
    }
}
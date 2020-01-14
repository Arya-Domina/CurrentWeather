package com.example.currentweather.util

import com.example.currentweather.R

fun Double.convertKtoC(): Double = this - 273.15

fun Int?.convertDegreeToDirection(): Int {
    if (this == null) return R.string.wind_direction_un
    val listDirections = listOf(
        R.string.wind_direction_n,
        R.string.wind_direction_ne,
        R.string.wind_direction_e,
        R.string.wind_direction_se,
        R.string.wind_direction_s,
        R.string.wind_direction_sw,
        R.string.wind_direction_w,
        R.string.wind_direction_nw,
        R.string.wind_direction_n
    )
    return try {
        listDirections[(this + 22.5).div(45).toInt()]
    } catch (e: IndexOutOfBoundsException) {
        Logger.log("Util", "convertDegreeToDirection: err", e)
        R.string.wind_direction_un
    }
}

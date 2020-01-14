package com.example.currentweather.util

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
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

fun ImageView.startRotateAnimation() {
    val animation = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    animation.interpolator = LinearInterpolator()
    animation.duration = 1300
    animation.repeatCount = -1
    animation.repeatMode = Animation.RESTART

    this.startAnimation(animation)
}

fun ImageView.stopAnimation() {
    this.clearAnimation()
}
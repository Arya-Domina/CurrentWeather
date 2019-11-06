package com.example.currentweather.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.currentweather.models.WeatherResponse
import com.google.gson.Gson

class PreferenceHelper(context: Context) {

    companion object {
        private const val WEATHER = "weather"
        private const val WIDGET_COLOR_NUMBER = "widget_color_"
    }

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val gson by lazy { Gson() }

    fun getWeather(): WeatherResponse {
        return try {
            gson.fromJson(sharedPreferences.getString(WEATHER, ""), WeatherResponse::class.java)
        } catch (e: Exception) {
            Logger.log("PreferenceHelper", "getWeather: response isn't stored")
            WeatherResponse()
        }
    }

    fun saveWeather(weatherResponse: WeatherResponse) {
        try {
            sharedPreferences.edit()
                ?.putString(WEATHER, gson.toJson(weatherResponse))
                ?.apply()
            Logger.log("PreferenceHelper", "saveWeather success")
        } catch (e: Exception) {
            Logger.log("PreferenceHelper", "saveWeather: weatherResponse wasn't stored", e)
        }
    }

    fun getColorNumber(widgetId: Int): Int {
        return sharedPreferences.getInt(WIDGET_COLOR_NUMBER + widgetId, 0)
    }

    fun saveColorNumber(widgetId: Int, colorNumber: Int) {
        sharedPreferences.edit().putInt(WIDGET_COLOR_NUMBER + widgetId, colorNumber).apply()
    }

}
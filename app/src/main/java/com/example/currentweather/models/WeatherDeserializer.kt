package com.example.currentweather.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class WeatherDeserializer : JsonDeserializer<WeatherResponse>, BaseDeserializer {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WeatherResponse {
        var weatherResponse = WeatherResponse()
        json?.asJsonObject?.let {
            weatherResponse = WeatherResponse(
                Coordination(
                    it.getJsonObject("coord")?.getDouble("lon") ?: 0.0,
                    it.getJsonObject("coord")?.getDouble("lat") ?: 0.0
                ),
                it.getJsonArrayElement("weather")?.getString("main"),
                it.getJsonArrayElement("weather")?.getString("description")?.capitalize(),
                it.getJsonObject("main")?.getDouble("temp"),
                it.getJsonObject("main")?.getInt("pressure"),
                it.getJsonObject("main")?.getInt("humidity"),
                it.getInt("visibility"),
                it.getJsonObject("wind")?.getDouble("speed"),
                it.getJsonObject("wind")?.getInt("deg"),
                it.getJsonObject("clouds")?.getInt("all"),
                it.getLong("dt"),
                it.getJsonObject("sys")?.getString("country"),
                it.getJsonObject("sys")?.getLong("sunrise"),
                it.getJsonObject("sys")?.getLong("sunset"),
                it.getLong("timezone"),
                it.getLong("id"),
                it.getString("name")
            )
        }
        return weatherResponse
    }
}
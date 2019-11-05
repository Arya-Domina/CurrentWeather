package com.example.currentweather.models

import com.example.currentweather.util.Logger
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class WeatherDeserializer : JsonDeserializer<WeatherResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WeatherResponse {
        Logger.log("WeatherDeserializer", "start")
        var weatherResponse = WeatherResponse()
        json?.asJsonObject?.let {
            Logger.log("WeatherDeserializer", "json: $json")
            weatherResponse = WeatherResponse(
                Coordination(
                    it.get("coord").asJsonObject.get("lon").asDouble,
                    it.get("coord").asJsonObject.get("lat").asDouble
                ),
                it.get("weather").asJsonArray[0].asJsonObject.get("main").asString,
                it.get("weather").asJsonArray[0].asJsonObject.get("description").asString.capitalize(),
                it.get("main").asJsonObject.get("temp").asDouble,
                it.get("main").asJsonObject.get("pressure").asInt,
                it.get("main").asJsonObject.get("humidity").asInt,
                it.get("visibility").asInt,
                it.get("wind").asJsonObject.get("speed").asDouble,
                it.get("wind").asJsonObject.get("deg").asInt,
                it.get("clouds").asJsonObject.get("all").asInt,
                it.get("dt").asLong,
                it.get("sys").asJsonObject.get("country").asString,
                it.get("sys").asJsonObject.get("sunrise").asLong,
                it.get("sys").asJsonObject.get("sunset").asLong,
                it.get("timezone").asLong,
                it.get("id").asLong,
                it.get("name").asString
            )
        }
        return weatherResponse
    }
}
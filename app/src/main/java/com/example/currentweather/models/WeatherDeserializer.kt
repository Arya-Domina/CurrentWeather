package com.example.currentweather.models

import com.example.currentweather.util.Logger
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

    private fun JsonObject.getJsonArrayElement(memberName: String): JsonObject? {
        return try {
            this.get(memberName).asJsonArray[0].asJsonObject
        } catch (th: Throwable) {
            null
        }
    }

    private fun JsonObject.getJsonObject(memberName: String): JsonObject? {
        return try {
            this.get(memberName).asJsonObject
        } catch (th: Throwable) {
            null
        }
    }

    private fun JsonObject.getDouble(memberName: String): Double? {
        return try {
            this.get(memberName).asDouble
        } catch (th: Throwable) {
            null
        }
    }

    private fun JsonObject.getInt(memberName: String): Int? {
        return try {
            this.get(memberName).asInt
        } catch (th: Throwable) {
            null
        }
    }

    private fun JsonObject.getLong(memberName: String): Long? {
        return try {
            this.get(memberName).asLong
        } catch (th: Throwable) {
            null
        }
    }

    private fun JsonObject.getString(memberName: String): String? {
        return try {
            this.get(memberName).asString
        } catch (th: Throwable) {
            null
        }
    }
}
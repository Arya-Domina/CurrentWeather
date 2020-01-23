package com.example.currentweather.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ForecastDeserializer : JsonDeserializer<ForecastResponse>, BaseDeserializer {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ForecastResponse {
        var forecastResponse = ForecastResponse()
        val forecastList = arrayListOf<Forecast>()
        json?.asJsonObject?.getAsJsonArray("list")?.let { jsonArray ->
            jsonArray.forEach {
                forecastList.add(
                    Forecast(
                        it.asJsonObject.getLong("dt"),
                        it.asJsonObject.getJsonObject("main")?.getDouble("temp"),
                        it.asJsonObject.getJsonObject("main")?.getDouble("feels_like"),
                        it.asJsonObject.getJsonObject("main")?.getInt("pressure"),
                        it.asJsonObject.getJsonObject("main")?.getInt("humidity"),
                        it.asJsonObject.getJsonArrayElement("weather")?.getString("main"),
                        it.asJsonObject.getJsonArrayElement("weather")?.getString("description")?.capitalize(),
                        it.asJsonObject.getJsonObject("clouds")?.getInt("all"),
                        it.asJsonObject.getJsonObject("wind")?.getDouble("speed"),
                        it.asJsonObject.getJsonObject("wind")?.getInt("deg"),
                        it.asJsonObject.getString("dt_txt")
                    )
                )

            }
        }

        json?.asJsonObject?.getJsonObject("city")?.let {
            forecastResponse = ForecastResponse(
                it.getString("name"),
                it.getString("country"),
                it.getLong("timezone"),
                Coordination(
                    it.getJsonObject("coord")?.getDouble("lon") ?: 0.0,
                    it.getJsonObject("coord")?.getDouble("lat") ?: 0.0
                ),
                it.getLong("sunrise"),
                it.getLong("sunset"),
                forecastList
            )
        }
        return forecastResponse
    }
}
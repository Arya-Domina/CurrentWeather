package com.example.currentweather.models

class ForecastResponse(
    cityName: String? = null,
    countryCode: String? = null,
    coordination: Coordination? = null,
    sunrise: Long? = null,
    sunset: Long? = null,
    timeZone: Long? = null,
    val forecast: List<ForecastItem> = listOf()
) : BaseResponse(cityName, countryCode, coordination, sunrise, sunset, timeZone) {

    override fun toString(): String {
        return "ForecastResponse(" +
                super.toString() +
                "Forecast=${forecast.toString()})"
    }
}
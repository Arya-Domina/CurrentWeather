package com.example.currentweather.models

sealed class Parameter {
    data class City(val cityName: String) : Parameter()
    data class Id(val id: Long) : Parameter()
    data class Coord(val coordination: Coordination) : Parameter()
}

package com.example.currentweather.models

class Coordination(var longitude: Double, var latitude: Double) {
    override fun toString(): String {
        return "Coord(lon=$longitude, lat=$latitude)"
    }
}
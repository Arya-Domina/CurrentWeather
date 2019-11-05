package com.example.currentweather

class Constants {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
        const val APP_ID_KEY = "4da864cde0bb4fdcaa253dc7440fb51a"
        const val INTERVAL = 600 // 60 * 10 = 10 min in s
        const val DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm"
    }
}
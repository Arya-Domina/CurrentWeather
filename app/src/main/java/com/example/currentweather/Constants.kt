package com.example.currentweather

class Constants {
    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
        const val APP_ID_KEY = "4da864cde0bb4fdcaa253dc7440fb51a"
        const val INTERVAL = 600 // 60 * 10 = 10 min in s
        const val DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm"
        const val TIME_FORMAT = "HH:mm"
        const val DEFAULT_CITY = "London"

        // Weather Widget
        const val DEFAULT_NUMBER = 0
        const val RED_NUMBER = 1
        const val GREEN_NUMBER = 2
        const val BLUE_NUMBER = 3
        const val ACTION_UPDATE_APPWIDGET = "action.UPDATE_WIDGET"
        const val EXTRA_ID_APPWIDGET = "extra.ID_WIDGET"
        const val DOUBLE_CLICK_DELAY = 1000L
    }
}
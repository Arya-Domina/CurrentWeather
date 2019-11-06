package com.example.currentweather.util

import android.util.Log
import com.example.currentweather.BuildConfig

class Logger {
    companion object {
        fun log(tag: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.v(tag, msg)
            }
        }
        fun log(tag: String, msg: String, tr: Throwable) {
            if (BuildConfig.DEBUG) {
                Log.v(tag, msg, tr)
            }
        }
    }
}
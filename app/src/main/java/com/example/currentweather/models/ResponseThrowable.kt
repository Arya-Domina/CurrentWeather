package com.example.currentweather.models

import androidx.annotation.StringRes

class ResponseThrowable(exception: Throwable, @StringRes val stringRes: Int) : Throwable(exception) {
}
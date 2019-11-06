package com.example.currentweather.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import com.example.currentweather.Constants.Companion.BLUE_NUMBER
import com.example.currentweather.Constants.Companion.DEFAULT_NUMBER
import com.example.currentweather.Constants.Companion.GREEN_NUMBER
import com.example.currentweather.Constants.Companion.RED_NUMBER
import com.example.currentweather.R
import com.example.currentweather.models.Params
import com.example.currentweather.repository.WeatherRepository
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import kotlinx.android.synthetic.main.weather_widget_config.*
import org.koin.android.ext.android.inject

class WeatherWidgetConfigure : Activity() {

    private val repository: WeatherRepository by inject()
    private val preferenceHelper: PreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_widget_config)
        Logger.log("WeatherWidgetConfigure", "onCreate")

        val widgetId = defineWidgetId()
        val resultIntent = createIntent(widgetId)
        setListener(resultIntent, widgetId)
    }

    private fun defineWidgetId(): Int {
        val id = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        Logger.log("WeatherWidgetConfigure", "defineWidgetId: $id")
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Logger.log("WeatherWidgetConfigure", "INVALID_APPWIDGET_ID")
            finish()
        }
        return id
    }

    private fun createIntent(widgetId: Int): Intent {
        return Intent().also {
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_CANCELED, it)
        }
    }

    private fun defineColorNumber(): Int {
        return when (radio_group_color.checkedRadioButtonId) {
            R.id.radio_red -> RED_NUMBER
            R.id.radio_green -> GREEN_NUMBER
            R.id.radio_blue -> BLUE_NUMBER
            else -> DEFAULT_NUMBER
        }
    }

    private fun setListener(resultIntent: Intent, widgetId: Int) {
        button.setOnClickListener {
            val colorNumber = defineColorNumber()
            Logger.log("WeatherWidgetConfigure", "click, color: $colorNumber")
            preferenceHelper.saveColorNumber(widgetId, colorNumber)

            repository
                .getCurrentWeather(Pair(Params.CityName, preferenceHelper.getWeather().cityName))
                .subscribe({ response ->
                    WeatherWidgetProvider().update(
                        this,
                        AppWidgetManager.getInstance(this),
                        widgetId,
                        response.temperature,
                        response.date,
                        colorNumber
                    )
                }, {
                    Logger.log("WeatherWidgetConfigure", "request err", it)
                })

            setResult(RESULT_OK, resultIntent)
            Logger.log("WeatherWidgetConfigure", "finish ok, $widgetId")
            finish()
        }
    }

}
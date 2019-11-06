package com.example.currentweather.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.currentweather.Constants.Companion.ACTION_UPDATE_APPWIDGET
import com.example.currentweather.Constants.Companion.BLUE_NUMBER
import com.example.currentweather.Constants.Companion.EXTRA_ID_APPWIDGET
import com.example.currentweather.Constants.Companion.GREEN_NUMBER
import com.example.currentweather.Constants.Companion.RED_NUMBER
import com.example.currentweather.Constants.Companion.TIME_FORMAT
import com.example.currentweather.R
import com.example.currentweather.models.Params
import com.example.currentweather.repository.WeatherRepository
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import com.example.currentweather.util.convertKtoC
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class WeatherWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val repository: WeatherRepository by inject()
    private val preferenceHelper: PreferenceHelper by inject()

    fun update(
        context: Context, manager: AppWidgetManager, widgetId: Int,
        temperature: Double?, date: Long?, colorNumber: Int? = null
    ) {
        val temperatureText = if (temperature != null) {
            context.resources.getString(R.string.temperature_widget, temperature.convertKtoC())
        } else {
            context.resources.getString(R.string.nan)
        }
        val timeText = if (date != null) {
            SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(date * 1000)
        } else {
            context.resources.getString(R.string.unknown_city)
        }

        RemoteViews(context.packageName, R.layout.weather_widget)
            .upWeather(temperatureText)
            .upTime(timeText)
            .setOnClick(context, widgetId)
            .setColor(colorNumber ?: preferenceHelper.getColorNumber(widgetId))
            .upWidget(manager, widgetId)
    }

    private fun RemoteViews.upWeather(temperature: String): RemoteViews {
        this.setTextViewText(R.id.weather_text, temperature)
        return this
    }

    private fun RemoteViews.upTime(date: String): RemoteViews {
        this.setTextViewText(R.id.update_time_text, date)
        return this
    }

    private fun RemoteViews.setColor(colorNumber: Int): RemoteViews {
        val drawableRes = when (colorNumber) {
            RED_NUMBER -> R.drawable.background_red
            GREEN_NUMBER -> R.drawable.background_green
            BLUE_NUMBER -> R.drawable.background_blue
            else -> R.drawable.background_white
        }
        this.setInt(R.id.container, "setBackgroundResource", drawableRes)
        return this
    }

    private fun RemoteViews.setOnClick(context: Context, widgetId: Int): RemoteViews {
        this.setOnClickPendingIntent(
            R.id.container, PendingIntent.getBroadcast(
                context, widgetId,
                Intent(context, WeatherWidgetProvider::class.java).also {
                    it.action = ACTION_UPDATE_APPWIDGET
                    it.putExtra(EXTRA_ID_APPWIDGET, widgetId)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        return this
    }

    private fun RemoteViews.upWidget(manager: AppWidgetManager, widgetId: Int) {
        manager.updateAppWidget(widgetId, this)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, widgetIds: IntArray) {
        Logger.log("WeatherWidgetProvider", "onUpdate")
        widgetIds.forEach { widgetId ->
            Logger.log("WeatherWidgetProvider", "id: $widgetId")
            updateWidget(context, manager, widgetId)
        }
        super.onUpdate(context, manager, widgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Logger.log("WeatherWidgetProvider", "onReceive, action: ${intent?.action}")
        if (context != null
            && intent?.action.equals(ACTION_UPDATE_APPWIDGET)
            && intent?.hasExtra(EXTRA_ID_APPWIDGET) == true
        ) {
            updateWidget(
                context,
                AppWidgetManager.getInstance(context),
                intent.getIntExtra(EXTRA_ID_APPWIDGET, 0)
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        repository.getCurrentWeather(Pair(Params.CityName, preferenceHelper.getWeather().cityName))
            .subscribe({ response ->
                update(context, manager, widgetId, response.temperature, response.date)
            }, {
                Logger.log("WeatherWidgetProvider", "request err", it)
            })
    }

}
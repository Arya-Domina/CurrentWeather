package com.example.currentweather.ui

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currentweather.Constants.Companion.TIME_FORMAT
import com.example.currentweather.R
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.convertDegreeToDirection
import java.text.SimpleDateFormat
import java.util.*

class DetailsListAdapter : RecyclerView.Adapter<DetailsListHolder>() {

    private var array = SparseArray<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var orderedNameList = listOf(
        R.string.time_name,
        R.string.wind_name,
        R.string.sun_name,
        R.string.humidity_name,
        R.string.clouds_name,
        R.string.pressure_name
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsListHolder {
        return DetailsListHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pair_text_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return array.size()
    }

    override fun onBindViewHolder(holder: DetailsListHolder, position: Int) {
        val key = orderedNameList[position]
        holder.bind(key, array[key])
    }

    fun updateInfo(context: Context, weather: WeatherResponse) {
        array = createArray(context, weather)
    }

    private fun createArray(context: Context, weather: WeatherResponse): SparseArray<String> {
        val newArray = SparseArray<String>()
        newArray.put(
            R.string.wind_name,
            weather.windSpeed?.let {
                context.resources.getString(
                    R.string.wind_format,
                    weather.windSpeed,
                    context.resources.getString(weather.windDegree.convertDegreeToDirection())
                )
            }
        )
        newArray.put(
            R.string.pressure_name,
            context.resources.getString(R.string.pressure_format, weather.pressure)
        )
        newArray.put(
            R.string.humidity_name,
            context.resources.getString(R.string.humidity_format, weather.humidity)
        )
        newArray.put(
            R.string.clouds_name,
            context.resources.getString(R.string.clouds_format, weather.clouds)
        )
        newArray.put(
            R.string.time_name,
            weather.date?.let {
                SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(it * 1000)
            }
        )

        newArray.put(
            R.string.sun_name,
            weather.sunrise?.let { sunrise ->
                weather.sunset?.let { sunset ->
                    context.resources.getString(
                        R.string.sun_format,
                        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(sunrise * 1000),
                        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(sunset * 1000)
                    )
                }
            }
        )
        return newArray
    }

}
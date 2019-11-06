package com.example.currentweather.ui

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        holder.bind(array.keyAt(position), array.valueAt(position))
    }

    fun updateInfo(context: Context, weather: WeatherResponse) {
        array = createArray(context, weather)
    }

    private fun createArray(context: Context, weather: WeatherResponse): SparseArray<String> {
        val newArray = SparseArray<String>()
        if (weather.windSpeed != null && weather.windDegree != null) {
            newArray.put(
                R.string.wind_name,
                context.resources.getString(
                    R.string.wind_format,
                    weather.windSpeed,
                    context.resources.getString(weather.windDegree.convertDegreeToDirection())
                )
            )
        }
        weather.pressure?.let {
            newArray.put(
                R.string.pressure_name,
                context.resources.getString(R.string.pressure_format, it)
            )
        }
        weather.humidity?.let {
            newArray.put(
                R.string.humidity_name,
                context.resources.getString(R.string.humidity_format, it)
            )
        }
        weather.clouds?.let {
            newArray.put(
                R.string.clouds_name,
                context.resources.getString(R.string.clouds_format, it)
            )
        }
        weather.date?.let {
            newArray.put(
                R.string.time_name,
                SimpleDateFormat(
                    context.resources.getString(R.string.time_format), Locale.getDefault()
                ).format(it * 1000)
            )
        }
        weather.sunrise?.let { sunrise ->
            weather.sunset?.let { sunset ->
                newArray.put(
                    R.string.sun_name, context.resources.getString(
                        R.string.sun_format,
                        SimpleDateFormat(
                            context.resources.getString(R.string.time_format), Locale.getDefault()
                        ).format(sunrise * 1000),
                        SimpleDateFormat(
                            context.resources.getString(R.string.time_format), Locale.getDefault()
                        ).format(sunset * 1000)
                    )
                )
            }
        }
        return newArray
    }

}
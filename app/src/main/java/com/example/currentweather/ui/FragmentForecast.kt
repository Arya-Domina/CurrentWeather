package com.example.currentweather.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.currentweather.MainViewModel
import com.example.currentweather.R
import com.example.currentweather.models.ForecastItem
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.util.*
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_forecast.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class FragmentForecast : BaseFragment<ForecastResponse>() {

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun requestUpdate(cityName: String?) {
        mainViewModel.updateForecast(cityName)
    }

    override fun changeCity(newCityName: String) {
        if (mainViewModel.isCityChanged(newCityName))
            mainViewModel.updateForecast(newCityName)
    }

    override fun observe(cityName: String?, textView: TextView) {
        mainViewModel.weatherForecast.observe(this, Observer { forecastResponse ->
            Logger.log("FragmentForecast", "observeForecast")
            textView.text = forecastResponse.cityName ?: resources.getString(R.string.no_data)
            updateView(forecastResponse)
        })
    }

    override fun updateView(weather: ForecastResponse) {
        graph_layout.removeAllViews()
        Logger.log(
            "FragmentForecast",
            "updateView, city: ${weather.cityName}, date: ${weather.forecast[0].date.convertSecondToString()}"
        )

        val maxY = weather.forecast.maxTemp().convertKtoC().also { ceil(it / 5) * 5 }
        val minY = weather.forecast.minTemp().convertKtoC().also { floor(it / 5) * 5 }
        Logger.log("FragmentForecast", "updateView, rounded maxY: $maxY, minY: $minY")

        val array = arrayListOf<DataPoint>()
        var title = weather.forecast[0].date.convertSecondToStringDay()
        weather.forecast.forEachIndexed { index, forecastItem ->
            array.add(
                DataPoint(
                    Date(forecastItem.date ?: 0),
                    forecastItem.temperature?.convertKtoC() ?: 0.0
                )
            )
            if (forecastItem.date.isMidnight()) {
                if (array.size > 2 && weather.forecast.size - index > 3) {
                    addGraph(title, array, maxY, minY)
                    array.clear()
                }
                if (weather.forecast.size - index > 3)
                    title = forecastItem.date.convertSecondToStringDay()
            }
        }
        Logger.log("FragmentForecast", "updateView array")

        addGraph(title, array, maxY, minY)
    }

    private fun addGraph(
        title: String, array: ArrayList<DataPoint>, maxY: Double, minY: Double
    ) {
        val series = LineGraphSeries<DataPoint>(array.toTypedArray())
        series.setOnDataPointTapListener { _, dataPoint ->
            Toast.makeText(
                context,
                "${dataPoint.x.convertToDataString()}: %.2f".format(dataPoint.y),
                Toast.LENGTH_SHORT
            ).show()
        }
        series.isDrawDataPoints = true

        val graph = GraphView(context)
        graph.addSeries(series)
        graph.title = title
        graph.labelRender(array.size)
        graph.setViewportBounds(array.last().x, array.first().x, maxY, minY)
        graph.setMargin()

        graph_layout.addView(graph)
    }

    private fun GraphView.labelRender(numLabels: Int) {
        gridLabelRenderer.setHumanRounding(false, true)
        gridLabelRenderer.numHorizontalLabels = numLabels
        gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    value.convertToTimeString()
                } else {
                    super.formatLabel(value, isValueX)
                }
            }
        }
    }

    private fun GraphView.setViewportBounds(
        maxX: Double, minX: Double, maxY: Double, minY: Double
    ) {
        viewport.isXAxisBoundsManual = true
        viewport.setMaxX(maxX)
        viewport.setMinX(minX)
        viewport.isYAxisBoundsManual = true
        viewport.setMaxY(maxY)
        viewport.setMinY(minY)
    }

    private fun GraphView.setMargin() {
        val dp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f,
            context?.resources?.displayMetrics
        ).toInt()
        val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp * 200)
        par.bottomMargin = 32 * dp
        par.marginEnd = 8 * dp
        layoutParams = par
    }

    private fun List<ForecastItem>.maxTemp(): Double {
        return find { it.temperature != null }?.temperature?.let { first ->
            var temp = first
            forEach { item ->
                if (item.temperature != null && item.temperature > temp)
                    temp = item.temperature
            }
            return temp
        } ?: 0.0
    }

    private fun List<ForecastItem>.minTemp(): Double {
        return find { it.temperature != null }?.temperature?.let { first ->
            var temp = first
            forEach { item ->
                if (item.temperature != null && item.temperature < temp)
                    temp = item.temperature
            }
            return temp
        } ?: 0.0
    }

}
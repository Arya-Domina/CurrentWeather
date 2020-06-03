package com.example.currentweather.ui

import android.graphics.Color
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
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_forecast.*
import kotlinx.android.synthetic.main.fragment_forecast.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

class FragmentForecast : BaseFragment<ForecastResponse>() {

    private val mainViewModel: MainViewModel by sharedViewModel()
    private val TAG_FIRST = "first"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)
        view.switch_legend.setOnClickListener {
            view.graph_layout.findViewWithTag<GraphView>(TAG_FIRST)?.let { firstGraph ->
                view.graph_layout.removeView(firstGraph)
                firstGraph.legendRenderer.isVisible = !firstGraph.legendRenderer.isVisible
                view.graph_layout.addView(firstGraph, 0)
            }
        }
        return view
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
            textView.text = forecastResponse.cityName ?: getString(R.string.no_data)
            updateView(forecastResponse)
        })
    }

    override fun updateView(weather: ForecastResponse) {
        graph_layout.removeAllViews()
        Logger.log(
            "FragmentForecast",
            "updateView, city: ${weather.cityName}, date: ${weather.forecast[0].date.convertSecondToString()}"
        )

        val maxY = weather.forecast.maxTempRounded()
        val minY = weather.forecast.minTempRounded()
        Logger.log("FragmentForecast", "updateView, rounded maxY: $maxY, minY: $minY")

        val arrayTemp = arrayListOf<DataPoint>()
        val arrayFeels = arrayListOf<DataPoint>()
        val arrayRain = arrayListOf<DataPoint>()
        val arraySnow = arrayListOf<DataPoint>()
        var title = weather.forecast[0].date.convertSecondToStringDay()
        weather.forecast.forEachIndexed { index, forecastItem ->
            forecastItem.addToArray(arrayTemp, forecastItem.temperature)
            forecastItem.addToArray(arrayFeels, forecastItem.temperatureFeels)
            weather.forecast.addToArrayNotNull(arrayRain, index) { it.rain }
            weather.forecast.addToArrayNotNull(arraySnow, index) { it.snow }

            if (forecastItem.date.isMidnight()) {
                if (arrayTemp.size > 2 && weather.forecast.size - index > 3) {
                    addGraph(title, arrayTemp, arrayFeels, arrayRain, arraySnow, maxY, minY)
                    arrayTemp.clear()
                    arrayFeels.clear()
                    arrayRain.clear()
                    arraySnow.clear()
                }
                if (weather.forecast.size - index > 3)
                    title = forecastItem.date.convertSecondToStringDay()
            }
        }

        addGraph(title, arrayTemp, arrayFeels, arrayRain, arraySnow, maxY, minY)
    }

    private fun ForecastItem.addToArray(array: ArrayList<DataPoint>, value: Double?) {
        array.add(
            DataPoint(
                Date(date ?: 0),
                value?.convertKtoC() ?: 0.0
            )
        )
    }

    private inline fun List<ForecastItem>.addToArrayNotNull(
        array: ArrayList<DataPoint>, index: Int,
        selector: (ForecastItem) -> Double?
    ) {
        val value = selector(get(index))?.let { it }
            ?: if (index != 0 && selector(get(index - 1)) != null ||
                index != size - 1 && selector(get(index + 1)) != null
            ) 0.0 else null
        if (value != null) array.add(
            DataPoint(
                Date(get(index).date ?: 0),
                value
            )
        )
    }

    private fun addGraph(
        title: String,
        arrayTemp: ArrayList<DataPoint>, arrayFeels: ArrayList<DataPoint>,
        arrayRain: ArrayList<DataPoint>, arraySnow: ArrayList<DataPoint>,
        maxY: Double, minY: Double
    ) {
        val seriesTemp = LineGraphSeries<DataPoint>(arrayTemp.toTypedArray())
        seriesTemp.setOnDataPointTapListener { _, dataPoint ->
            Toast.makeText(
                context,
                "${dataPoint.x.convertToDataString()}: %.2f".format(dataPoint.y),
                Toast.LENGTH_SHORT
            ).show()
        }
        seriesTemp.isDrawDataPoints = true
        val seriesFeels = LineGraphSeries<DataPoint>(arrayFeels.toTypedArray())
        val seriesRain = LineGraphSeries<DataPoint>(arrayRain.toTypedArray())
        val seriesSnow = LineGraphSeries<DataPoint>(arraySnow.toTypedArray())

        val graph = GraphView(context)
        graph.addSeries(seriesTemp)
        graph.addSeries(seriesFeels)
        graph.secondScale.addSeries(seriesRain)
        graph.secondScale.addSeries(seriesSnow)

        seriesTemp.color = Color.BLUE
        seriesFeels.color = Color.CYAN
        seriesRain.color = Color.argb(120, 172, 218, 255)
        seriesRain.isDrawBackground = true
        seriesSnow.color = Color.rgb(255, 255, 255)
        seriesSnow.isDrawBackground = true
        seriesSnow.backgroundColor = Color.argb(120, 255, 255, 255)
        graph.gridLabelRenderer.verticalLabelsColor = Color.rgb(0, 0, 180)
        graph.gridLabelRenderer.verticalLabelsSecondScaleColor = Color.rgb(80, 130, 255)

        if (graph_layout.findViewWithTag<GraphView>(TAG_FIRST) == null) {
            graph.tag = TAG_FIRST
            graph.legendRenderer.backgroundColor = Color.argb(150, 255, 255, 255)
            graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
            seriesTemp.title = getString(R.string.legend_temp)
            seriesFeels.title = getString(R.string.legend_feels)
            seriesRain.title = getString(R.string.legend_rain)
            seriesSnow.title = getString(R.string.legend_snow)
        }

        graph.title = title
        graph.labelRender(arrayTemp.size)
        graph.setViewportBounds(arrayTemp.last().x, arrayTemp.first().x, maxY, minY)
        graph.setSecondViewportBounds(arrayRain, arraySnow)
        graph.setMargin()

        graph_layout.addView(graph)
    }

    private fun GraphView.labelRender(numLabels: Int) {
        gridLabelRenderer.setHumanRounding(false, true)
        gridLabelRenderer.numHorizontalLabels = numLabels
        gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    getString(R.string.hour, value.convertToTimeString())
                } else {
                    super.formatLabel(value, isValueX) + "°"
                }
            }
        }
        secondScale.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return getString(R.string.millimeters, value)
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

    private fun GraphView.setSecondViewportBounds(
        arrayRain: ArrayList<DataPoint>, arraySnow: ArrayList<DataPoint>
    ) {
        val maxRain = arrayRain.maxBy { it.y }?.y ?: 0.0
        val maxSnow = arraySnow.maxBy { it.y }?.y ?: 0.0
        secondScale.setMinY(0.0)
        secondScale.setMaxY(max(maxRain, maxSnow).let { if (it < 5) 5.0 else ceil(it / 2) * 2 })
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

    private fun List<ForecastItem>.maxTempRounded(): Double {
        return find { it.temperature != null }?.temperature?.let { first ->
            var temp = first
            forEach { item ->
                if (item.temperature != null && item.temperature > temp)
                    temp = item.temperature
                if (item.temperatureFeels != null && item.temperatureFeels > temp)
                    temp = item.temperatureFeels
            }
            ceil(temp.convertKtoC() / 5) * 5
        } ?: 0.0
    }

    private fun List<ForecastItem>.minTempRounded(): Double {
        return find { it.temperature != null }?.temperature?.let { first ->
            var temp = first
            forEach { item ->
                if (item.temperature != null && item.temperature < temp)
                    temp = item.temperature
                if (item.temperatureFeels != null && item.temperatureFeels < temp)
                    temp = item.temperatureFeels
            }
            floor(temp.convertKtoC() / 5) * 5
        } ?: 0.0
    }

}
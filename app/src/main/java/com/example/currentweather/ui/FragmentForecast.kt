package com.example.currentweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.currentweather.MainViewModel
import com.example.currentweather.R
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.util.*
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_forecast.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

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
        mainViewModel.updateForecast(cityName)
    }

    override fun updateView(weather: ForecastResponse) {
        Logger.log(
            "FragmentForecast",
            "updateView, city: ${weather.cityName}, date: ${weather.forecast[0].date.convertSecondToString()}"
        )
        val pointCount = 11
        val array = arrayListOf<DataPoint>()
        weather.forecast.filterIndexed { index, _ -> index < pointCount }.forEach { forecastItem ->
            array.add(
                DataPoint(
                    Date(forecastItem.date ?: 0),
                    forecastItem.temperature?.convertKtoC() ?: 0.0
                )
            )
        }
        Logger.log("FragmentForecast", "updateView array")

        val series = LineGraphSeries<DataPoint>(array.toTypedArray())
        series.setOnDataPointTapListener { _, dataPoint ->
            Toast.makeText(
                context,
                "${dataPoint.x.convertToDataString()}: %.2f".format(dataPoint.y),
                Toast.LENGTH_SHORT
            ).show()
        }
        series.isDrawDataPoints = true
        graph.removeAllSeries()
        graph.addSeries(series)
        Logger.log("FragmentForecast", "updateView add series")

        //graph.gridLabelRenderer.setHumanRounding(false, true)
        //graph.gridLabelRenderer.numHorizontalLabels = (pointCount / 2) + 1
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    value.convertToTimeString()
                } else {
                    super.formatLabel(value, isValueX)
                }
            }
        }

        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(array.first().x)
        graph.viewport.setMaxX(array.last().x)
        Logger.log("FragmentForecast", "updateView render")

    }

    /*override fun updateView(weather: ForecastResponse) {
        Logger.log(
            "FragmentForecast",
            "updateView, city: ${weather.cityName}, date: ${weather.forecast[0].date.convertSecondToString()}"
        )

        val array = arrayListOf<DataPoint>()
//        val array2 = arrayListOf<DataPoint>()

        weather.forecast.filterIndexed { index, forecastItem -> index < 11 }.forEachIndexed { index, forecastItem ->
//            array.add(DataPoint(index.toDouble(), forecastItem.temperature?.convertKtoC() ?: 0.0))
            array.add(DataPoint(Date(forecastItem.date?.convertStoMS() ?: 0), forecastItem.temperature?.convertKtoC() ?: 0.0))
//            array.add(DataPoint(Date(forecastItem.date ?: 0), forecastItem.temperature?.convertKtoC() ?: 0.0))
//            array.add(DataPoint(forecastItem.date?.toDouble() ?: 0.0, forecastItem.temperature?.convertKtoC() ?: 0.0))
//            if (forecastItem.date?.convertSecondToString()?.endsWith("00:00") == true) {
//                array2.add(DataPoint(Date(forecastItem.date), 2.0))
//            }

        }
        Logger.log("FragmentForecast", "updateView array")

        val series = LineGraphSeries<DataPoint>(array.toTypedArray())
        series.setOnDataPointTapListener { _, dataPoint ->
//            Toast.makeText(context, "${dataPoint.x.convertToDataString()}: %.2f".format(dataPoint.y), Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "${dataPoint.x.convertToDataString()}: %.2f".format(dataPoint.y), Toast.LENGTH_SHORT).show()
        }
        series.isDrawDataPoints = true

//        val paint = Paint()
//        paint.setStyle(Paint.Style.STROKE)
//        paint.setStrokeWidth(10f)
//        paint.setPathEffect(DashPathEffect(floatArrayOf(8f, 5f), 0f))
//        series.setCustomPaint(paint)

//        val seriesDays = BarGraphSeries<DataPoint>(array2.toTypedArray())
        graph.removeAllSeries()
        graph.addSeries(series)
        //series.color = Color.rgb((Math.random() * 256).toInt(), (Math.random() * 256).toInt(), (Math.random() * 256).toInt())
        //series.title = weather.cityName
        //graph.legendRenderer.isVisible = true
        Logger.log("FragmentForecast", "updateView add series")

//        seriesDays.dataWidth
//        graph.addSeries(seriesDays)
//        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(context)
//        graph.gridLabelRenderer.numHorizontalLabels = 6
//        graph.gridLabelRenderer.setHumanRounding(true, graph.gridLabelRenderer.isHumanRoundingY)
        graph.gridLabelRenderer.setHumanRounding(false, true)
        graph.gridLabelRenderer.numHorizontalLabels = (array.size / 2) + 1
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(weather.forecast.first().date?.convertStoMS()?.toDouble() ?: 0.0)
//        graph.viewport.setMaxX(weather.forecast.last().date?.convertStoMS()?.toDouble() ?: 0.0)
        //graph.viewport.setMaxX(weather.forecast[10].date?.convertStoMS()?.toDouble() ?: 0.0)
        graph.viewport.setMaxX(weather.forecast[array.size - 1].date?.convertStoMS()?.toDouble() ?: 0.0)
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    return value.convertSecondToString()
                } else {
                    super.formatLabel(value, isValueX)
                }
            }
        }
        Logger.log("FragmentForecast", "updateView render")
//        graph.viewport.isScalable = true
//        graph.viewport.isScrollable = true
    }
    */

}
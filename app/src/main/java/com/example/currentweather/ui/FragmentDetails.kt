package com.example.currentweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currentweather.MainViewModel
import com.example.currentweather.R
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.Logger
import com.example.currentweather.util.convertKtoC
import kotlinx.android.synthetic.main.fragment_details.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentDetails : BaseFragment<WeatherResponse>() {

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        view.recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = DetailsListAdapter()
        }
        return view
    }

    override fun requestUpdate(cityName: String?) {
        mainViewModel.updateWeather(cityName)
    }

    override fun changeCity(newCityName: String) {
        if (mainViewModel.isCityChanged(newCityName))
            mainViewModel.updateWeather(newCityName)
    }

    override fun observe(cityName: String?, textView: TextView) {
        mainViewModel.weatherData.observe(this, Observer { weatherResponse ->
            Logger.log("FragmentDetails", "observeDetails")
            textView.text = weatherResponse.cityName ?: resources.getString(R.string.no_data)
            updateView(weatherResponse)
        })
        mainViewModel.updateWeather(cityName)
    }

    override fun updateView(weather: WeatherResponse) {
        view?.let { view ->
            view.temperature.text = weather.temperature.let {
                resources.getString(R.string.temperature_degree, it?.convertKtoC())
            } ?: resources.getString(R.string.nan)
            view.description.text =
                weather.weatherDescription ?: resources.getString(R.string.no_data)
            (view.recycler_view.adapter as DetailsListAdapter).updateInfo(activity!!, weather)
        }
    }
}
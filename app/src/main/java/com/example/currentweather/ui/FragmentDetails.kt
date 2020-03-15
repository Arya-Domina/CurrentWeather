package com.example.currentweather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currentweather.R
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.convertKtoC
import kotlinx.android.synthetic.main.fragment_details.view.*

class FragmentDetails : BaseFragment<WeatherResponse>() {

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
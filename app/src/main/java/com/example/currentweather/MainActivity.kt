package com.example.currentweather

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.example.currentweather.ui.BaseFragment
import com.example.currentweather.ui.FragmentDetails
import com.example.currentweather.ui.FragmentForecast
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val preferenceHelper: PreferenceHelper by inject()
    private val input by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private var fragmentType: String = preferenceHelper.getFragmentType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.log("MainActivity", "onCreate")

        updateFragment()

        bindView()
        subscribe()
        setListeners()
    }

    override fun onDestroy() {
        Logger.log("MainMainActivity", "onDestroy")
        preferenceHelper.saveFragmentType(fragmentType)
        mainViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (edit_city.visibility == View.VISIBLE) {
            showCityLabel()
        } else {
            super.onBackPressed()
        }
    }

    private fun bindView() {
        container.setOnRefreshListener { // TODO to Listeners
            when (fragmentType) { // TODO to Fragments ?
                FragmentForecast::class.java.simpleName -> {
                    mainViewModel.updateForecast(city_name.text.toString().trim())
                }
                else -> {
                    mainViewModel.updateWeather(city_name.text.toString().trim())
                }
            }
        }
    }

    private fun subscribe() {
//        mainViewModel.weatherData.observe(this, Observer { weatherResponse ->
//            city_name.text = weatherResponse.cityName ?: resources.getString(R.string.no_data)
////            weatherResponse::class.java
////            (supportFragmentManager.findFragmentById(R.id.layout) as FragmentDetails).updateView(weatherResponse)
//            supportFragmentManager.findFragmentById(R.id.layout).takeIf { it is FragmentDetails }?.let {
//                (it as FragmentDetails).updateView(weatherResponse)
//            }
//        })
//        //
//        mainViewModel.weatherForecast.observe(this, Observer { forecastResponse ->
//            city_name.text = forecastResponse.cityName ?: resources.getString(R.string.no_data)
////            (supportFragmentManager.findFragmentById(R.id.layout) as FragmentForecast).updateView(forecastResponse)
//            supportFragmentManager.findFragmentById(R.id.layout).takeIf { it is FragmentForecast }?.let {
//                (it as FragmentForecast).updateView(forecastResponse)
//            }
//        })

        mainViewModel.isLoadingNow.observe(this, Observer {
            container.isRefreshing = it
        })
        mainViewModel.errorStringRes.observe(this, Observer { stringRes ->
            error_text.setText(stringRes)
        })
    }

    private fun setListeners() {
        city_name.setOnClickListener {
            showEditCity()
        }
        edit_city.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                && keyEvent.action == KeyEvent.ACTION_DOWN
            ) {
                showCityLabel()
                return@setOnKeyListener true
            }
            false
        }
        layout.setOnClickListener {
            if (edit_city.visibility == View.VISIBLE) {
                showCityLabel()
            }
        }
        switch_view.setOnClickListener {
            if (fragmentType == FragmentForecast::class.java.simpleName) fragmentType = FragmentDetails::class.java.simpleName
            else if (fragmentType == FragmentDetails::class.java.simpleName) fragmentType = FragmentForecast::class.java.simpleName
            Logger.log("MainActivity", "setListeners switch to $fragmentType")
            updateFragment(city_name.text.toString().trim())
        }
    }

    private fun showCityLabel() {
        city_name.visibility = View.VISIBLE
        edit_city.visibility = View.GONE
        layout.setBackgroundResource(R.color.background)
        input.hideSoftInputFromWindow(container.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        when (fragmentType) {
            FragmentForecast::class.java.simpleName -> {
                mainViewModel.changeCityF(edit_city.text.toString().trim())
                Logger.log("MainActivity", "showCityLabel type: $fragmentType, ${edit_city.text}")
            }
            else -> {
                mainViewModel.changeCity(edit_city.text.toString().trim())
                Logger.log("MainActivity", "showCityLabel type: $fragmentType, ${edit_city.text}")
            }
        }
    }

    private fun showEditCity() {
        city_name.visibility = View.GONE
        edit_city.setText(city_name.text)
        edit_city.visibility = View.VISIBLE
        edit_city.requestFocus()
        edit_city.selectAll()
        layout.setBackgroundResource(R.color.shadow_background)
        input.showSoftInput(edit_city, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun updateFragment(cityName: String? = null) {
        when (fragmentType) {
            FragmentForecast::class.java.simpleName -> {
                Logger.log("MainActivity", "updateFragment Forecast")
                supportFragmentManager.replaceFragment(FragmentForecast())

                mainViewModel.weatherData.removeObservers(this)
                mainViewModel.weatherForecast.observe(this, Observer { forecastResponse ->
                    city_name.text = forecastResponse.cityName ?: resources.getString(R.string.no_data)
                    Logger.log("MainActivity", "updateFragment test")
                    supportFragmentManager.findFragmentById(R.id.layout).takeIf { it is FragmentForecast }?.let {
                        (it as FragmentForecast).updateView(forecastResponse)
                    }
                })

                mainViewModel.updateForecast(cityName)
            }
            else -> { // FragmentDetails
                Logger.log("MainActivity", "updateFragment else (Details)")
                supportFragmentManager.replaceFragment(FragmentDetails())

                mainViewModel.weatherForecast.removeObservers(this)
                mainViewModel.weatherData.observe(this, Observer { weatherResponse ->
                    city_name.text = weatherResponse.cityName ?: resources.getString(R.string.no_data)
                    supportFragmentManager.findFragmentById(R.id.layout).takeIf { it is FragmentDetails }?.let {
                        (it as FragmentDetails).updateView(weatherResponse)
                    }
                })

                mainViewModel.updateWeather(cityName)
            }
        }
    }

    private fun FragmentManager.replaceFragment(newFragment: BaseFragment<*>) {
        Logger.log("MainActivity", "replaceFragment new: ${newFragment::class.java.simpleName}")
        val transaction = beginTransaction()
        transaction.replace(R.id.layout, newFragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.commitNow()
    }

}

package com.example.currentweather

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currentweather.ui.DetailsListAdapter
import com.example.currentweather.util.convertKtoC
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val input by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()
        subscribe()
        setListeners()
    }

    override fun onDestroy() {
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
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DetailsListAdapter()
        }
    }

    private fun subscribe() {
        mainViewModel.weatherData.observe(this, Observer { weather ->
            city_name.text = weather.cityName ?: resources.getString(R.string.unknown_city)
            temperature.text = weather.temperature?.let {
                resources.getString(R.string.temperature_degree, it.convertKtoC())
            } ?: resources.getString(R.string.nan)
            description.text =
                weather.weatherDescription ?: resources.getString(R.string.unknown_city)

            (recycler_view.adapter as DetailsListAdapter).updateInfo(this, weather)
        })

        mainViewModel.isLoadingNow.observe(this, Observer {
            if (it) {
                progress_bar.visibility = View.VISIBLE
            } else {
                progress_bar.visibility = View.INVISIBLE
            }
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
    }

    private fun showCityLabel() {
        city_name.visibility = View.VISIBLE
        edit_city.visibility = View.INVISIBLE
        layout.setBackgroundResource(R.color.background)
        input.hideSoftInputFromWindow(container.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        mainViewModel.changeCity(edit_city.text.toString().trim())
    }

    private fun showEditCity() {
        city_name.visibility = View.INVISIBLE
        edit_city.setText(city_name.text)
        edit_city.visibility = View.VISIBLE
        edit_city.requestFocus()
        edit_city.selectAll()
        layout.setBackgroundResource(R.color.shadow_background)
        input.showSoftInput(edit_city, InputMethodManager.SHOW_IMPLICIT)
    }

}

package com.example.currentweather

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.currentweather.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subscribe()
        setListeners()
    }

    override fun onDestroy() {
        mainViewModel.onDestroy()
        super.onDestroy()
    }

    private fun subscribe() {
        mainViewModel.weatherData.observe(this, Observer {
            Logger.log("MainActivity", "getting $it")
            text_view.text = it.cityName ?: resources.getString(R.string.unknown_city)
        })
    }

    private fun setListeners() {
        text_view.setOnClickListener {
            mainViewModel.changeCity(edit_text.text.toString().trim())
        }

        edit_text.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                && keyEvent.action == KeyEvent.ACTION_DOWN
            ) {
                mainViewModel.changeCity(edit_text.text.toString().trim())
                return@setOnKeyListener true
            }
            false
        }
    }

}

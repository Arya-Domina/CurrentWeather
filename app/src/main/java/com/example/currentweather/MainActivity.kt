package com.example.currentweather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.currentweather.models.Params
import com.example.currentweather.repository.WeatherRepository
import com.example.currentweather.util.Logger
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val weatherRepository: WeatherRepository by inject()
    private var d: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        d = weatherRepository.getCurrentWeather(Pair(Params.CityName, null))
            .subscribe({
                Logger.log("MainActivity", "it: $it")
            }, {
                Logger.log("MainActivity", "err", it)
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        d?.dispose()
    }
}

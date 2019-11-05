package com.example.currentweather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.currentweather.rest.ApiService
import com.example.currentweather.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by inject()
    private var d: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        d = apiService.getCurrentWeather("London")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
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

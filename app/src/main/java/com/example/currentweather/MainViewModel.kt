package com.example.currentweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.models.Params
import com.example.currentweather.models.WeatherException
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.repository.WeatherRepository
import com.example.currentweather.util.Logger
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val weatherForecast = MutableLiveData<ForecastResponse>()
    val weatherData = MutableLiveData<WeatherResponse>()
    val isLoadingNow = MutableLiveData<Boolean>(false)
    val errorStringRes = MutableLiveData<Int>(R.string.empty)
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    init {
        updateWeather()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    fun changeCity(newCityName: String) {
        if ((newCityName.isNotBlank() && newCityName != weatherData.value?.cityName)
            || (errorStringRes.value != R.string.empty)
        ) {
            updateWeather(newCityName)
        }
    }

    fun updateWeather(newCityName: String? = null) {
        isLoadingNow.value = true
        val disposable = repository.getCurrentWeather(Pair(Params.CityName, newCityName))
            .subscribe({
                Logger.log("MainViewModel", "updateWeather: $it")
                weatherData.postValue(it)
            }, {
                Logger.log("MainViewModel", "updateWeather: err", it)
                isLoadingNow.postValue(false)
                errorStringRes.postValue((it as WeatherException).stringRes)
            }, {
                Logger.log("MainViewModel", "updateWeather: onComplete")
                isLoadingNow.postValue(false)
                errorStringRes.postValue(R.string.empty)
            })
        compositeDisposable.addAll(disposable)
    }

    fun updateForecast(cityName: String) {
        val disposable = repository.getForecast(cityName)
            .subscribe({
                Logger.log("MainViewModel", "updateForecast: $it")
                weatherForecast.postValue(it)
            }, {
                Logger.log("MainViewModel", "updateForecast: err", it)
            })
        compositeDisposable.addAll(disposable)
    }

}
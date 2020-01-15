package com.example.currentweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currentweather.models.Params
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.repository.WeatherRepository
import com.example.currentweather.util.Logger
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    val weatherData = MutableLiveData<WeatherResponse>()
    val isLoadingNow = MutableLiveData<Boolean>(false)
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    init {
        updateWeather()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    fun changeCity(newCityName: String) {
        if (newCityName.isNotBlank() && newCityName != weatherData.value?.cityName) {
            updateWeather(newCityName)
        }
    }

    private fun updateWeather(newCityName: String? = null) {
        isLoadingNow.value = true
        val disposable = repository.getCurrentWeather(Pair(Params.CityName, newCityName))
            .subscribe({
                Logger.log("MainViewModel", "updateWeather: $it")
                weatherData.postValue(it)
            }, {
                Logger.log("MainViewModel", "updateWeather: err", it)
                isLoadingNow.postValue(false)
            }, {
                isLoadingNow.postValue(false)
            })
        compositeDisposable.addAll(disposable)
    }

}
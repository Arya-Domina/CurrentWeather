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
//                weatherData.value = it
                weatherData.postValue(it)
//                isLoadingNow.value = false
                isLoadingNow.postValue(false)
            }, {
                Logger.log("MainViewModel", "updateWeather: err", it)
                isLoadingNow.value = false
            })
        compositeDisposable.addAll(disposable)
    }

}
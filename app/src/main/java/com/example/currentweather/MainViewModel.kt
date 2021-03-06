package com.example.currentweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currentweather.models.ForecastResponse
import com.example.currentweather.models.Parameter
import com.example.currentweather.models.ResponseThrowable
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

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun isCityChanged(newCityName: String): Boolean = (
            (newCityName.isNotBlank() && newCityName != weatherData.value?.cityName)
                    || (errorStringRes.value != R.string.empty))

    fun updateWeather(newCityName: String? = null) {
        isLoadingNow.value = true
        val disposable = repository.getCurrentWeather(newCityName?.let { Parameter.City(it) })
            .subscribe({
                Logger.log("MainViewModel", "updateWeather: $it")
                weatherData.value = it
            }, {
                isLoadingNow.value = false
                if (it is ResponseThrowable) errorStringRes.value = it.stringRes
                else Logger.log("MainViewModel", "updateWeather: err", it)
            }, {
                Logger.log("MainViewModel", "updateWeather: onComplete")
                isLoadingNow.value = false
                errorStringRes.value = R.string.empty
            })
        compositeDisposable.addAll(disposable)
    }

    fun updateForecast(newCityName: String? = null) {
        isLoadingNow.value = true
        val disposable = repository.getForecast(newCityName)
            .subscribe({
                Logger.log("MainViewModel", "updateForecast: $it")
                weatherForecast.value = it
                isLoadingNow.value = false
                errorStringRes.value = R.string.empty
            }, {
                isLoadingNow.value = false
                if (it is ResponseThrowable) errorStringRes.value = it.stringRes
                else Logger.log("MainViewModel", "updateForecast: err", it)
            })
        compositeDisposable.addAll(disposable)
    }

}
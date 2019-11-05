package com.example.currentweather

import com.example.currentweather.repository.LocalRepository
import com.example.currentweather.repository.NetworkRepository
import com.example.currentweather.repository.WeatherRepository
import com.example.currentweather.rest.Client
import com.example.currentweather.util.PreferenceHelper
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {

    single { WeatherRepository() }
    single { LocalRepository() }
    single { NetworkRepository() }
    single { PreferenceHelper(androidContext()) }
    single { Client().getApi() }

    viewModel { MainViewModel(get()) }

}
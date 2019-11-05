package com.example.currentweather

import com.example.currentweather.rest.Client
import org.koin.dsl.module

val koinModule = module {

    single { Client().getApi() }

}
package com.example.currentweather.rest

import com.example.currentweather.Constants.Companion.BASE_URL
import com.example.currentweather.models.WeatherDeserializer
import com.example.currentweather.models.WeatherResponse
import com.example.currentweather.util.Logger
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class Client {
    private val apiService: ApiService by lazy {
        Logger.log("Client", "init api")
        return@lazy createApiService()
    }

    private fun createApiService(): ApiService {
        Logger.log("Client", "create api")
        val r = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapter(
                        WeatherResponse::class.java, WeatherDeserializer()
                    ).create()
                )
            )
            .build()
            .create(ApiService::class.java)
        Logger.log("Client", "created")
        return r
    }

    fun getApi(): ApiService {
        Logger.log("Client", "return api")
        return apiService
    }
}
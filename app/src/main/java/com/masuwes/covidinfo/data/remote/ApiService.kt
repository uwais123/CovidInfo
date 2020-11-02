package com.masuwes.covidinfo.data.remote

import com.masuwes.covidinfo.helper.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    private val client = OkHttpClient.Builder().build()
    private val retrofitClient = Retrofit.Builder()
        .client(client)
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> buildService(service: Class<T>): T = retrofitClient.create(service)
}
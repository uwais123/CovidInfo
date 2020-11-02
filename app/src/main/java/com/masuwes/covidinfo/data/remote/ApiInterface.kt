package com.masuwes.covidinfo.data.remote

import com.masuwes.covidinfo.data.model.ResponseCountry
import com.masuwes.covidinfo.data.model.ResponseSummary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

//    https://api.covid19api.com/summary
    @GET("summary")
    suspend fun getSummary(): Response<ResponseSummary>

    @GET("dayone/country/{country_name}")
    suspend fun getCountryData(@Path("country_name") country_name: String): Response<List<ResponseCountry>>
}
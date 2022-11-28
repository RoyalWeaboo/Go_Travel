package com.binar.c5team.gotravel.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientAirport {
    private const val BASE_URL = "https://gist.githubusercontent.com/romi-ari/"

    val instance : RestfulApiAirport by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RestfulApiAirport::class.java)
    }
}
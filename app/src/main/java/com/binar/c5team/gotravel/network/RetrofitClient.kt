package com.binar.c5team.gotravel.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL_AIRPORT = "https://gist.githubusercontent.com/romi-ari/"
    private const val BASE_URL_API = "https://final-project-be-production.up.railway.app/api/v1/"

    val airportInstance : RestfulApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_AIRPORT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RestfulApi::class.java)
    }

    val apiInstance : RestfulApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RestfulApi::class.java)
    }

    fun apiProfile(accessToken: String): RestfulApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor(accessToken))
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL_API)
            .client(client)
            .build()

        return retrofit.create(RestfulApi::class.java)
    }
}
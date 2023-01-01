package com.binar.c5team.gotravel.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private const val BASE_URL_API = "https://gotravel-ilms4lrona-as.a.run.app/api/v1/"

    private val logging: HttpLoggingInterceptor
        get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        }

    private val loggingClient = OkHttpClient.Builder().addInterceptor(logging).build()

    fun apiWithToken(accessToken: String): RestfulApi {
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

    fun apiWithoutToken(): RestfulApiWithoutToken =
        Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(loggingClient)
            .build()
            .create(RestfulApiWithoutToken::class.java)

}
package com.binar.c5team.gotravel.network

import com.binar.c5team.gotravel.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RestfulApi {
    //Airport Data
    @GET("71f3256a894b72849fbe017b28b86a20/raw/bc08d9cb69bccf073c557bad500ee1b75be5710d/indonesia-international-airport.json")
    fun getAirportData() : Call<List<AirportDataItem>>

    //Register
    @POST("register")
    fun register(@Body register : RegisterData) : Call<RegisterResponse>

    //Login
    @POST("login")
    fun login(@Body login : LoginData) : Call<LoginResponse>

    //Profile
    @GET("profile")
    fun getProfile() : Call<ProfileResponse>
}
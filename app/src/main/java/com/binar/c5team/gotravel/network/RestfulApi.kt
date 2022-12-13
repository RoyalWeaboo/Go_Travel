package com.binar.c5team.gotravel.network

import com.binar.c5team.gotravel.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RestfulApi {
    //Airport Data
    @GET("airport")
    fun getAirportData() : Call<AirportResponse>

    //Register
    @POST("register")
    fun register(@Body register : RegisterData) : Call<RegisterResponse>

    //Login
    @POST("login")
    fun login(@Body login : LoginData) : Call<LoginResponse>

    //Profile
    @GET("profile")
    fun getProfile() : Call<ProfileResponse>

    //Flight Data
    @GET("flight")
    fun getFlight() : Call<FlightResponse>

    //Ticket Booking
    @POST("booking")
    fun postBooking(@Body bookingData : BookingData) : Call<BookingResponse>
}
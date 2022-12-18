package com.binar.c5team.gotravel.network

import com.binar.c5team.gotravel.model.*
import retrofit2.Call
import retrofit2.http.*

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

    //Get Booking (For History)
    @GET("booking")
    fun getBooking() : Call<BookingResponse>

    //Ticket Booking
    @POST("booking")
    fun postBooking(@Body bookingData : BookingData) : Call<BookingPostResponse>

    //Get Wishlist
    @GET("whislist")
    fun getWishlist() : Call<WishlistResponse>

    //Add Wishlist
    @POST("whislist")
    fun postWishlist(@Body wishlistData : WishlistData) : Call <WishlistPostResponse>

    //Delete Wishlist
    @DELETE("whislist/{id}")
    fun deleteWishlist(@Path("id") id : Int) : Call<Int>

}
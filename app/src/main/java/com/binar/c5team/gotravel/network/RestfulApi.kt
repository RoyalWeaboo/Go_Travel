package com.binar.c5team.gotravel.network

import com.binar.c5team.gotravel.model.*
import okhttp3.MultipartBody
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

    //update profile
    @PUT("updateUser")
    fun putProfile(@Body profileData: ProfileData) : Call<PutProfileResponse>

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

    //Post Payment Confirmation
    @Multipart
    @POST("confirmation")
    fun postPaymentConfirmation(@Part file : MultipartBody.Part) : Call<ConfirmationPostResponse>

    //Post Profile Image
    @Multipart
    @PUT("updateProfileUser")
    fun putProfileImage(@Part file : MultipartBody.Part) : Call<ProfileImagePutResponse>

}
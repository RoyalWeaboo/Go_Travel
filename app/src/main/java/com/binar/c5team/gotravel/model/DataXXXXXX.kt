package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class DataXXXXXX(
    @SerializedName("bookings")
    val bookings: List<Booking>
)
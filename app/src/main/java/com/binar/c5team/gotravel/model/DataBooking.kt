package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class DataBooking(
    @SerializedName("booking")
    val booking: List<Booking>
)
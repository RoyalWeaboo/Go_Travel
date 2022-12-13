package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("data")
    val `data`: DataBooking,
    @SerializedName("meta")
    val meta: MetaXX,
    @SerializedName("status")
    val status: String
)
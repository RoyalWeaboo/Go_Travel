package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class BookingPostResponse(
    @SerializedName("data")
    val `data`: DataXXXXX,
    @SerializedName("status")
    val status: String
)
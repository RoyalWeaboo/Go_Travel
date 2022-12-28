package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("data")
    val `data`: DataXXXXXX,
    @SerializedName("meta")
    val meta: MetaXXXX,
    @SerializedName("status")
    val status: String
)
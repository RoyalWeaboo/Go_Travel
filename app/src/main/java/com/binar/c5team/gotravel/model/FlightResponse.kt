package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class FlightResponse(
    @SerializedName("data")
    val `data`: DataXXXXXXX,
    @SerializedName("meta")
    val meta: MetaXXXXX,
    @SerializedName("status")
    val status: String
)
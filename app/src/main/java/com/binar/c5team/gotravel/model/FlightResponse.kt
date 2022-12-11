package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class FlightResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("status")
    val status: String
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("flights")
    val flights: List<Flight>
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("airports")
    val airports: List<Airport>
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class AirportDataItem(
    @SerializedName("City")
    val city: String,
    @SerializedName("Code")
    val code: String,
    @SerializedName("Country")
    val country: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Province")
    val province: String
)
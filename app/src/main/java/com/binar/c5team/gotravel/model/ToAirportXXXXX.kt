package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class ToAirportXXXXX(
    @SerializedName("city")
    val city: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)
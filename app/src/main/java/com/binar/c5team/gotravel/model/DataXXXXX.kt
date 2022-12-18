package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class DataXXXXX(
    @SerializedName("baggage")
    val baggage: Any,
    @SerializedName("booking_date")
    val bookingDate: Any,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("food")
    val food: Any,
    @SerializedName("homephone")
    val homephone: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Any,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("mobilephone")
    val mobilephone: Any,
    @SerializedName("name")
    val name: Any,
    @SerializedName("totalprice")
    val totalprice: Any,
    @SerializedName("updatedAt")
    val updatedAt: String
)
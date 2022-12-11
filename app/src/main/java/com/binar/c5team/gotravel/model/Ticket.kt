package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_booking")
    val idBooking: Int,
    @SerializedName("price")
    val price: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class TicketX(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_booking")
    val idBooking: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
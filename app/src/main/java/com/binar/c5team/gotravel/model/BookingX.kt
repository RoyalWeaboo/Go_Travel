package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class BookingX(
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("booking_date")
    val bookingDate: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("Flight")
    val flight: Any,
    @SerializedName("food")
    val food: Any,
    @SerializedName("homephone")
    val homephone: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Any,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("mobilephone")
    val mobilephone: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("Ticket")
    val ticket: Any,
    @SerializedName("totalprice")
    val totalprice: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
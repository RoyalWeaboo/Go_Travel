package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("booking_date")
    val bookingDate: Any,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("Flight")
    val flight: FlightX,
    @SerializedName("food")
    val food: Boolean,
    @SerializedName("homephone")
    val homephone: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Int,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("mobilephone")
    val mobilephone: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("Ticket")
    val ticket: TicketX,
    @SerializedName("totalprice")
    val totalprice: Any,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("User")
    val user: UserX
)
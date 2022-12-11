package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("createdAt")
    val createdAt: String,
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
    @SerializedName("seat")
    val seat: Int,
    @SerializedName("Ticket")
    val ticket: Ticket,
    @SerializedName("totalprice")
    val totalprice: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("User")
    val user: User
)
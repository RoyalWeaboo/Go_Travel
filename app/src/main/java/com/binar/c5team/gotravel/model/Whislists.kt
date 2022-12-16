package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class Whislists(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("Flight")
    val flight: FlightXX,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Int,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("User")
    val user: UserXX
)
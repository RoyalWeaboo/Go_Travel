package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class WhislistsXX(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("Flight")
    val flight: FlightXXXXXX,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Int,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
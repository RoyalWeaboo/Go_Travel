package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class WhislistsX(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("Flight")
    val flight: FlightXXXX,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Int,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
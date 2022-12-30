package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class WhislistsXXX(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_flight")
    val idFlight: Int,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
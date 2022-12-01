package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("address")
    val address: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("no_ktp")
    val noKtp: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("username")
    val username: String
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("address")
    val address: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("no_ktp")
    val noKtp: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("Whislists")
    val whislists: List<Any>
)
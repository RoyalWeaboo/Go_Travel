package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class DataXXXXXXXX(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_user")
    val idUser: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)
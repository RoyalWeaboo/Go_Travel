package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class PlaneXXX(
    @SerializedName("code")
    val code: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)
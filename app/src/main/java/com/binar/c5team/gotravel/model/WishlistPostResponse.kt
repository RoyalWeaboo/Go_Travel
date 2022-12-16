package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class WishlistPostResponse(
    @SerializedName("data")
    val `data`: DataXXX,
    @SerializedName("status")
    val status: String
)
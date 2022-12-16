package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class WishlistResponse(
    @SerializedName("data")
    val `data`: DataXXXX,
    @SerializedName("meta")
    val meta: MetaXXX,
    @SerializedName("status")
    val status: String
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class NotificationPostResponse(
    @SerializedName("data")
    val `data`: DataXXXXXXXX,
    @SerializedName("status")
    val status: String
)
package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("data")
    val `data`: DataXXXXXXXXX,
    @SerializedName("meta")
    val meta: MetaXXXXXX,
    @SerializedName("status")
    val status: String
)
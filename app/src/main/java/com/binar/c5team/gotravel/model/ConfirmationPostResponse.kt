package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class ConfirmationPostResponse(
    @SerializedName("status")
    val status: String
)
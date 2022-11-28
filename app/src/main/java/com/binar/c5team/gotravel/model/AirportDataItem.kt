package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class AirportDataItem(
    @SerializedName("IATA")
    val iATA: String,
    @SerializedName("ICAO")
    val iCAO: String,
    @SerializedName("Nama bandara")
    val namaBandara: String,
    @SerializedName("No")
    val no: Int,
    @SerializedName("Provinsi")
    val provinsi: String
)
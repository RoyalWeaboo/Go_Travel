package com.binar.c5team.gotravel.model


import com.google.gson.annotations.SerializedName

data class FlightXXXXXXX(
    @SerializedName("arrival_time")
    val arrivalTime: String,
    @SerializedName("available_seats")
    val availableSeats: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("departure_time")
    val departureTime: String,
    @SerializedName("flight_date")
    val flightDate: String,
    @SerializedName("FromAirport")
    val fromAirport: FromAirportXXXXX,
    @SerializedName("from_airport_id")
    val fromAirportId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_plane")
    val idPlane: Int,
    @SerializedName("kelas")
    val kelas: String,
    @SerializedName("Plane")
    val plane: PlaneXXXXX,
    @SerializedName("price")
    val price: Int,
    @SerializedName("ToAirport")
    val toAirport: ToAirportXXXXX,
    @SerializedName("to_airport_id")
    val toAirportId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)
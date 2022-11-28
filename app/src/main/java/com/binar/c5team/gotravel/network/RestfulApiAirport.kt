package com.binar.c5team.gotravel.network

import com.binar.c5team.gotravel.model.AirportDataItem
import retrofit2.Call
import retrofit2.http.GET

interface RestfulApiAirport {
    @GET("71f3256a894b72849fbe017b28b86a20/raw/bc08d9cb69bccf073c557bad500ee1b75be5710d/indonesia-international-airport.json")
    fun getAirportData() : Call<List<AirportDataItem>>
}
package com.binar.c5team.gotravel.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.*
import com.binar.c5team.gotravel.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlightViewModel : ViewModel() {
    var flightLiveData : MutableLiveData<FlightResponse> = MutableLiveData()

    fun getFlightListData(): MutableLiveData<FlightResponse> {
        return flightLiveData
    }

    fun callFlightApi(token : String) {
        RetrofitClient.apiFlight(token).getFlight()
            .enqueue(object : Callback<FlightResponse> {
                override fun onResponse(
                    call: Call<FlightResponse>,
                    response: Response<FlightResponse>
                ) {
                    if (response.isSuccessful) {
                        flightLiveData.postValue(response.body())
                    } else {
                        Log.d("Fetch Flight Data Failed", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<FlightResponse>, t: Throwable) {
                    Log.d("Fetch Flight Data Error", call.toString())
                }

            })
    }

}
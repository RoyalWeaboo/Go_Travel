package com.binar.c5team.gotravel.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.AirportResponse
import com.binar.c5team.gotravel.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AirportViewModel : ViewModel() {
    var loading = MutableLiveData<Boolean>()
    var airportList: MutableLiveData<AirportResponse> = MutableLiveData()

    fun getAirportListData(): MutableLiveData<AirportResponse> {
        return airportList
    }

    fun callAirportApi() {
        loading.postValue(true)
        RetrofitClient.airportInstance.getAirportData()
            .enqueue(object : Callback<AirportResponse> {
                override fun onResponse(
                    call: Call<AirportResponse>,
                    response: Response<AirportResponse>
                ) {
                    if (response.isSuccessful) {
                        airportList.postValue(response.body())
//                        Log.d("data", response.body().toString())
                    } else {
                        airportList.postValue(response.body())
//                        Log.d("data", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<AirportResponse>, t: Throwable) {
                    Log.d("data error", call.toString())
                    loading.postValue(false)
                }

            })
    }
}
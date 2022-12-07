package com.binar.c5team.gotravel.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.AirportDataItem
import com.binar.c5team.gotravel.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AirportViewModel : ViewModel() {
    var airportList: MutableLiveData<List<AirportDataItem>> = MutableLiveData()

    fun getAirportListData(): MutableLiveData<List<AirportDataItem>> {
        return airportList
    }

    fun callAirportApi() {
        RetrofitClient.airportInstance.getAirportData()
            .enqueue(object : Callback<List<AirportDataItem>> {
                override fun onResponse(
                    call: Call<List<AirportDataItem>>,
                    response: Response<List<AirportDataItem>>
                ) {
                    if (response.isSuccessful) {
                        airportList.postValue(response.body())
//                        Log.d("data", response.body().toString())
                    } else {
                        airportList.postValue(response.body())
//                        Log.d("data", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<List<AirportDataItem>>, t: Throwable) {
                    Log.d("data error", call.toString())
                }

            })
    }
}
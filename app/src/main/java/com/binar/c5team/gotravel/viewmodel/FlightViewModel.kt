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
    var bookingLiveData : MutableLiveData<BookingResponse> = MutableLiveData()

    fun getFlightListData(): MutableLiveData<FlightResponse> {
        return flightLiveData
    }

    fun getBookingLD():MutableLiveData<BookingResponse> {
        return bookingLiveData
    }

    fun callFlightApi(token : String) {
        RetrofitClient.apiWithToken(token).getFlight()
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

    fun postBookingApi(token : String, id_flight : Int, id_user : Int, seat : Int, baggage : Int, food : Boolean, name : String, homePhone : String, mobilePhone : String, totalPrice : Int) {
        RetrofitClient.apiWithToken(token).postBooking(BookingData(id_flight, id_user, seat, baggage, food, name, homePhone, mobilePhone, totalPrice))
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    if (response.isSuccessful) {
                        bookingLiveData.postValue(response.body())
                    } else {
                        Log.d("Booking Failed", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    Log.d("Booking Error", call.toString())
                }

            })
    }

}
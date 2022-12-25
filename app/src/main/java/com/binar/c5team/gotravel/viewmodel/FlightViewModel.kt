package com.binar.c5team.gotravel.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.*
import com.binar.c5team.gotravel.network.RetrofitClient
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlightViewModel : ViewModel() {
    var loading = MutableLiveData<Boolean>()
    var flightLiveData : MutableLiveData<FlightResponse> = MutableLiveData()
    var bookingLiveData : MutableLiveData<BookingResponse> = MutableLiveData()
    var postBookingLiveData : MutableLiveData<BookingPostResponse> = MutableLiveData()
    var wishlistLiveData : MutableLiveData<WishlistResponse> = MutableLiveData()
    var postWishlistLiveData : MutableLiveData<WishlistPostResponse> = MutableLiveData()
    var delWishlistLiveData : MutableLiveData<Int> = MutableLiveData()
    var postConfirmationLiveData : MutableLiveData<ConfirmationPostResponse> = MutableLiveData()

    fun getFlightListData(): MutableLiveData<FlightResponse> {
        return flightLiveData
    }

    fun getBookingLD():MutableLiveData<BookingResponse> {
        return bookingLiveData
    }

    fun getPostBookingLD():MutableLiveData<BookingPostResponse> {
        return postBookingLiveData
    }

    fun getWishlistLD():MutableLiveData<WishlistResponse> {
        return wishlistLiveData
    }

    fun postWishlistLD():MutableLiveData<WishlistPostResponse> {
        return postWishlistLiveData
    }

    fun deleteWishlistLD():MutableLiveData<Int> {
        return delWishlistLiveData
    }

    fun postConfirmationLD():MutableLiveData<ConfirmationPostResponse> {
        return postConfirmationLiveData
    }

    fun callFlightApi(token : String) {
        loading.postValue(true)
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
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<FlightResponse>, t: Throwable) {
                    Log.d("Fetch Flight Data Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun callWishlistApi(token : String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).getWishlist()
            .enqueue(object : Callback<WishlistResponse> {
                override fun onResponse(
                    call: Call<WishlistResponse>,
                    response: Response<WishlistResponse>
                ) {
                    if (response.isSuccessful) {
                        wishlistLiveData.postValue(response.body())
                    } else {
                        Log.d("Fetch Wishlist Data Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                    Log.d("Fetch Wishlist Data Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun callBookingApi(token: String){
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).getBooking()
            .enqueue(object : Callback<BookingResponse>{
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    if(response.isSuccessful){
                        bookingLiveData.postValue(response.body())
                    }else{
                        Log.d("Fetch Booking Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    Log.d("Fetch Booking Error", call.toString())
                    loading.postValue(false)
                }

            }
            )
    }

    fun postBookingApi(token : String, id_flight : Int, id_user : Int, baggage : Int, food : Boolean, name : String, homePhone : String, mobilePhone : String, totalPrice : Int, bookingDate : String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).postBooking(BookingData(id_flight, id_user, baggage, food, name, homePhone, mobilePhone, totalPrice, bookingDate))
            .enqueue(object : Callback<BookingPostResponse> {
                override fun onResponse(
                    call: Call<BookingPostResponse>,
                    response: Response<BookingPostResponse>
                ) {
                    loading.postValue(false)
                    if (response.isSuccessful) {
                        postBookingLiveData.postValue(response.body())
                    } else {
                        Log.d("Booking Failed", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<BookingPostResponse>, t: Throwable) {
                    Log.d("Booking Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun postWishlistApi(token : String, id_flight : Int, id_user : Int) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).postWishlist(WishlistData(id_flight, id_user))
            .enqueue(object : Callback<WishlistPostResponse> {
                override fun onResponse(
                    call: Call<WishlistPostResponse>,
                    response: Response<WishlistPostResponse>
                ) {
                    if (response.isSuccessful) {
                        postWishlistLiveData.postValue(response.body())
                    } else {
                        Log.d("Booking Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<WishlistPostResponse>, t: Throwable) {
                    Log.d("Booking Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun callDeleteWishlist(token :String, id: Int) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).deleteWishlist(id)
            .enqueue(object : Callback<Int> {
                override fun onResponse(
                    call: Call<Int>,
                    response: Response<Int>
                ) {
                    if (response.isSuccessful) {
                        delWishlistLiveData.postValue(response.body())
                    } else {
                        Log.d("data", response.body().toString())
                    }
                    loading.postValue(false)
                    callWishlistApi(token)
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.d("delete failure", call.toString())
                    loading.postValue(false)
                    callWishlistApi(token)
                }
            })
    }

    fun postConfirmationPaymentImage(token :String, file : MultipartBody.Part) {
        loading.postValue(true)
        RetrofitClient.baseApiWithToken(token).postPaymentConfirmation(file)
            .enqueue(object : Callback<ConfirmationPostResponse> {
                override fun onResponse(
                    call: Call<ConfirmationPostResponse>,
                    response: Response<ConfirmationPostResponse>
                ) {
                    if (response.isSuccessful) {
                        postConfirmationLiveData.postValue(response.body())
                    } else {
                        Log.d("failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<ConfirmationPostResponse>, t: Throwable) {
                    Log.d("on failure", call.toString())
                    loading.postValue(false)
                }
            })
    }



}
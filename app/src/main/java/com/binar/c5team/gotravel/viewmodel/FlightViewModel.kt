package com.binar.c5team.gotravel.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.*
import com.binar.c5team.gotravel.network.RetrofitClient
import com.binar.c5team.gotravel.view.fragment.BookingFragment
import com.binar.c5team.gotravel.view.fragment.LoginFragment
import com.binar.c5team.gotravel.view.fragment.RoundBookingFragment
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlightViewModel : ViewModel() {
    //loading
    var loading = MutableLiveData<Boolean>()

    //user
    var registerLiveData: MutableLiveData<RegisterResponse> = MutableLiveData()
    var loginLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    var profileLiveData: MutableLiveData<ProfileResponse> = MutableLiveData()
    var putProfileDataLiveData: MutableLiveData<PutProfileResponse> = MutableLiveData()
    var profileImageLiveData: MutableLiveData<ProfileImagePutResponse> = MutableLiveData()
    var notificationLiveData: MutableLiveData<NotificationResponse> = MutableLiveData()
    var postNotificationLiveData: MutableLiveData<NotificationPostResponse> = MutableLiveData()

    //airport
    var airportList: MutableLiveData<AirportResponse> = MutableLiveData()

    //flight
    var postBookingLiveData: MutableLiveData<BookingPostResponse> = MutableLiveData()
    var flightLiveData: MutableLiveData<FlightResponse> = MutableLiveData()
    var bookingLiveData: MutableLiveData<BookingResponse> = MutableLiveData()
    var wishlistLiveData: MutableLiveData<WishlistResponse> = MutableLiveData()
    var postWishlistLiveData: MutableLiveData<WishlistPostResponse> = MutableLiveData()
    var delWishlistLiveData: MutableLiveData<Int> = MutableLiveData()
    var postConfirmationLiveData: MutableLiveData<ConfirmationPostResponse> = MutableLiveData()


    //user
    fun getRegisterData(): MutableLiveData<RegisterResponse> {
        return registerLiveData
    }

    fun postLoginData(): MutableLiveData<LoginResponse> {
        return loginLiveData
    }

    fun getProfileData(): MutableLiveData<ProfileResponse> {
        return profileLiveData
    }

    fun putProfileData(): MutableLiveData<PutProfileResponse> {
        return putProfileDataLiveData
    }

    fun putProfileImageData(): MutableLiveData<ProfileImagePutResponse> {
        return profileImageLiveData
    }

    fun getNotificationData(): MutableLiveData<NotificationResponse> {
        return notificationLiveData
    }

    fun postNotificationData(): MutableLiveData<NotificationPostResponse> {
        return postNotificationLiveData
    }

    //airport
    fun getAirportListData(): MutableLiveData<AirportResponse> {
        return airportList
    }

    //flight
    fun getFlightListData(): MutableLiveData<FlightResponse> {
        return flightLiveData
    }

    fun getBookingLD(): MutableLiveData<BookingResponse> {
        return bookingLiveData
    }

    fun getPostBookingLD(): MutableLiveData<BookingPostResponse> {
        return postBookingLiveData
    }

    fun getWishlistLD(): MutableLiveData<WishlistResponse> {
        return wishlistLiveData
    }

    fun postWishlistLD(): MutableLiveData<WishlistPostResponse> {
        return postWishlistLiveData
    }

    fun deleteWishlistLD(): MutableLiveData<Int> {
        return delWishlistLiveData
    }

    fun postConfirmationLD(): MutableLiveData<ConfirmationPostResponse> {
        return postConfirmationLiveData
    }

    //user
    fun callRegisterApi(
        username: String,
        fullname: String,
        email: String,
        password: String,
        dateBirth: String,
        gender: String,
        address: String
    ) {
        RetrofitClient.apiWithoutToken()
            .register(RegisterData(username, fullname, email, password, dateBirth, gender, address))
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        registerLiveData.postValue(response.body())
                    } else {
                        registerLiveData.postValue(response.body())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d("Register Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun postLoginApi(fragment: LoginFragment, username: String, password: String) {
        loading.value = true
        RetrofitClient.apiWithoutToken().login(LoginData(username, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        loginLiveData.value = response.body()
                        fragment.userId = response.body()!!.id
                        fragment.usernameRes = response.body()!!.username
                        fragment.token = response.body()!!.token
                        loading.value = false
                    } else {
                        Log.d("login response", response.body().toString())
                        loading.value = false
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("Login Data Error", call.toString())
                    loading.value = false
                }
            })
    }


    fun callProfileApi(token: String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).getProfile()
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        profileLiveData.postValue(response.body())
                    } else {
                        Log.d("Fetch Profile Data Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.d("Fetch Profile Data Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun putProfileData(
        token: String,
        no_ktp: String,
        gender: String,
        date_of_birth: String,
        address: String,
        email: String,
        name: String
    ) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token)
            .putProfile(ProfileData(no_ktp, gender, date_of_birth, address, email, name))
            .enqueue(object : Callback<PutProfileResponse> {
                override fun onResponse(
                    call: Call<PutProfileResponse>,
                    response: Response<PutProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        putProfileDataLiveData.postValue(response.body())
                    } else {
                        Log.d("failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<PutProfileResponse>, t: Throwable) {
                    Log.d("on failure", call.toString())
                    loading.postValue(false)
                }
            })
    }

    fun putProfileImage(token: String, file: MultipartBody.Part) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).putProfileImage(file)
            .enqueue(object : Callback<ProfileImagePutResponse> {
                override fun onResponse(
                    call: Call<ProfileImagePutResponse>,
                    response: Response<ProfileImagePutResponse>
                ) {
                    if (response.isSuccessful) {
                        profileImageLiveData.postValue(response.body())
                    } else {
                        Log.d("failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<ProfileImagePutResponse>, t: Throwable) {
                    Log.d("on failure", call.toString())
                    loading.postValue(false)
                }
            })
    }

    fun callNotificationApi(token: String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).getNotification()
            .enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    if (response.isSuccessful) {
                        notificationLiveData.postValue(response.body())
                    } else {
                        Log.d("Fetch Notification Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Log.d("Fetch Notification Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun postNotificationApi(token: String, message: String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).postNotification(NotificationData(message))
            .enqueue(object : Callback<NotificationPostResponse> {
                override fun onResponse(
                    call: Call<NotificationPostResponse>,
                    response: Response<NotificationPostResponse>
                ) {
                    if (response.isSuccessful) {
                        postNotificationLiveData.postValue(response.body())
                    } else {
                        Log.d("Post Notification Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<NotificationPostResponse>, t: Throwable) {
                    Log.d("Post Notification Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    //airport
    fun callAirportApi() {
        loading.postValue(true)
        RetrofitClient.apiWithoutToken().getAirportData()
            .enqueue(object : Callback<AirportResponse> {
                override fun onResponse(
                    call: Call<AirportResponse>,
                    response: Response<AirportResponse>
                ) {
                    if (response.isSuccessful) {
                        airportList.postValue(response.body())
                    } else {
                        airportList.postValue(response.body())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<AirportResponse>, t: Throwable) {
                    Log.d("data error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    //flight
    fun callFlightApi(token: String) {
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

    fun callWishlistApi(token: String) {
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

    fun callBookingApi(token: String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).getBooking()
            .enqueue(object : Callback<BookingResponse> {
                override fun onResponse(
                    call: Call<BookingResponse>,
                    response: Response<BookingResponse>
                ) {
                    if (response.isSuccessful) {
                        bookingLiveData.value = response.body()
                    } else {
                        Log.d("Fetch Booking Failed", response.body().toString())
                    }
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                    Log.d("Fetch Booking Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun postBookingApi(
        fragment: BookingFragment,
        token: String,
        id_flight: Int,
        id_user: Int,
        baggage: Int,
        food: Boolean,
        name: String,
        homePhone: String,
        mobilePhone: String,
        totalPrice: Int,
        bookingDate: String,
        trip_type: String
    ) {
        loading.value = true
        RetrofitClient.apiWithToken(token).postBooking(
            BookingData(
                id_flight,
                id_user,
                baggage,
                food,
                name,
                homePhone,
                mobilePhone,
                totalPrice,
                bookingDate,
                trip_type
            )
        )
            .enqueue(object : Callback<BookingPostResponse> {
                override fun onResponse(
                    call: Call<BookingPostResponse>,
                    response: Response<BookingPostResponse>
                ) {
                    if (response.isSuccessful) {
                        postBookingLiveData.value = response.body()
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val id = responseBody.data.id
                            fragment.bookingIds.add(id)
                        }
                        Log.d(
                            "id that should have been added is",
                            response.body()!!.data.id.toString()
                        )
                        loading.value = false
                    } else {
                        Log.d("Booking Failed", response.body().toString())
                        loading.value = false
                    }
                }

                override fun onFailure(call: Call<BookingPostResponse>, t: Throwable) {
                    Log.d("Booking Error", call.toString())
                    loading.value = false
                }

            })
    }

    fun postRoundBookingApi(
        fragment: RoundBookingFragment,
        token: String,
        id_flight: Int,
        id_user: Int,
        baggage: Int,
        food: Boolean,
        name: String,
        homePhone: String,
        mobilePhone: String,
        totalPrice: Int,
        bookingDate: String,
        trip_type: String
    ) {
        loading.value = true
        RetrofitClient.apiWithToken(token).postBooking(
            BookingData(
                id_flight,
                id_user,
                baggage,
                food,
                name,
                homePhone,
                mobilePhone,
                totalPrice,
                bookingDate,
                trip_type
            )
        )
            .enqueue(object : Callback<BookingPostResponse> {
                override fun onResponse(
                    call: Call<BookingPostResponse>,
                    response: Response<BookingPostResponse>
                ) {
                    if (response.isSuccessful) {
                        postBookingLiveData.value = response.body()
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val id = responseBody.data.id
                            fragment.bookingIds.add(id)
                        }
                        Log.d(
                            "id that should have been added is",
                            response.body()!!.data.id.toString()
                        )
                        loading.value = false
                    } else {
                        Log.d("Booking Failed", response.body().toString())
                        loading.value = false
                    }
                }

                override fun onFailure(call: Call<BookingPostResponse>, t: Throwable) {
                    Log.d("Booking Error", call.toString())
                    loading.value = false
                }

            })
    }


    fun postWishlistApi(token: String, id_flight: Int, id_user: Int) {
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

    fun callDeleteWishlist(token: String, id: Int) {
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

    fun postConfirmationPaymentImage(token: String, id: Int, file: MultipartBody.Part) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).postPaymentConfirmation(id, file)
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
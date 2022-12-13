package com.binar.c5team.gotravel.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.*
import com.binar.c5team.gotravel.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UserViewModel : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    var loginLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    var registerLiveData : MutableLiveData<RegisterResponse> = MutableLiveData()
    var profileLiveData : MutableLiveData<ProfileResponse> = MutableLiveData()

    fun getRegisterData(): MutableLiveData<RegisterResponse> {
        return registerLiveData
    }

    fun getProfileData(): MutableLiveData<ProfileResponse> {
        return profileLiveData
    }

    fun callRegisterApi(username : String, fullname : String, email : String, password:String, dateBirth : String, gender : String, address : String) {
        loading.postValue(true)
        RetrofitClient.apiInstance.register(RegisterData(username, fullname, email, password, dateBirth, gender, address))
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


    fun callProfileApi(token : String) {
        RetrofitClient.apiProfile(token).getProfile()
            .enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        profileLiveData.postValue(response.body())
                        Log.d("Fetch Profile Data Success", response.body().toString())
                    } else {
                        Log.d("Fetch Profile Data Failed", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.d("Fetch Profile Data Error", call.toString())
                }

            })
    }


}
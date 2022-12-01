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

    var loginLiveData: MutableLiveData<LoginResponse> = MutableLiveData()
    var registerLiveData : MutableLiveData<RegisterResponse> = MutableLiveData()
    var profileLiveData : MutableLiveData<ProfileResponse> = MutableLiveData()

    fun getRegisterData(): MutableLiveData<RegisterResponse> {
        return registerLiveData
    }

    fun getLoginData(): MutableLiveData<LoginResponse> {
        return loginLiveData
    }

    fun getProfileData(): MutableLiveData<ProfileResponse> {
        return profileLiveData
    }

    fun callRegisterApi(name : String, email : String, username:String, password:String, sex : String, dateBirth : Date) {
        RetrofitClient.apiInstance.register(RegisterData(name, email, username, password, sex, dateBirth))
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        registerLiveData.postValue(response.body())
                        Log.d("Register Success", response.body().toString())
                    } else {
                        Log.d("Register Failed", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Log.d("Register Error", call.toString())
                }

            })
    }

    fun callLoginApi(username : String, password : String) {
        RetrofitClient.apiInstance.login(LoginData(username,password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        loginLiveData.postValue(response.body())
                        Log.d("Login Data Success", response.body().toString())
                    } else {
                        Log.d("Login Data Failed", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("Login Data Error", call.toString())
                }

            })
    }

    fun callProfileApi(token : String) {
        RetrofitClient.apiInstance.getProfile(token)
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
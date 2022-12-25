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

class UserViewModel : ViewModel() {

    var loading = MutableLiveData<Boolean>()
    var registerLiveData : MutableLiveData<RegisterResponse> = MutableLiveData()
    var profileLiveData : MutableLiveData<ProfileResponse> = MutableLiveData()
    var putProfileDataLiveData : MutableLiveData<PutProfileResponse> = MutableLiveData()
    var profileImageLiveData : MutableLiveData<ProfileImagePutResponse> = MutableLiveData()

    fun getRegisterData(): MutableLiveData<RegisterResponse> {
        return registerLiveData
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
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).getProfile()
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
                    loading.postValue(false)
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.d("Fetch Profile Data Error", call.toString())
                    loading.postValue(false)
                }

            })
    }

    fun putProfileData(token : String, no_ktp : String, gender : String, date_of_birth : String, address : String, email : String, name : String) {
        loading.postValue(true)
        RetrofitClient.apiWithToken(token).putProfile(ProfileData(no_ktp, gender, date_of_birth, address, email, name))
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

    fun putProfileImage(token : String, file : MultipartBody.Part) {
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


}
package com.binar.c5team.gotravel.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binar.c5team.gotravel.model.AirportDataItem

class UserViewModel : ViewModel() {

    var airportList: MutableLiveData<List<AirportDataItem>> = MutableLiveData()

    fun getAirportListData(): MutableLiveData<List<AirportDataItem>> {
        return airportList
    }
}
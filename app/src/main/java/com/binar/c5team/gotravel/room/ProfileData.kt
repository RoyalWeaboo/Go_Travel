package com.binar.c5team.gotravel.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileData(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    @ColumnInfo(name = "no_ktp")
    var no_ktp : String,
    @ColumnInfo(name = "gender")
    var gender : String,
    @ColumnInfo(name = "date_of_birth")
    var date_of_birth : String,
    @ColumnInfo(name = "address")
    var address : String,
    @ColumnInfo(name = "email")
    var email : String,
    @ColumnInfo(name = "name")
    var name : String,
    @ColumnInfo(name = "username")
    var username : String
)
package com.binar.c5team.gotravel.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE


@Dao
interface ProfileDAO {
    @Insert(onConflict = REPLACE)
    fun insertProfile(note : ProfileData)

    @Query("SELECT * FROM profiledata")
    fun getProfile() : ProfileData

}
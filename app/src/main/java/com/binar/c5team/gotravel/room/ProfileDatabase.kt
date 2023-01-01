package com.binar.c5team.gotravel.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProfileData::class], version = 1)
abstract class ProfileDatabase : RoomDatabase() {

    abstract fun profileDao() : ProfileDAO

    companion object{
        private var INSTANCE : ProfileDatabase? = null

        fun getInstance(context : Context):ProfileDatabase? {
            if (INSTANCE == null){
                synchronized(ProfileDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ProfileDatabase::class.java,"Note.db").allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

    }


}
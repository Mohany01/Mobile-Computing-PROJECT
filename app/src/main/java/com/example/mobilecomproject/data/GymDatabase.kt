package com.example.mobilecomproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Booking::class], version = 4, exportSchema = false)
abstract class GymDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile private var INSTANCE: GymDatabase? = null

        fun getInstance(context: Context): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "gym_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
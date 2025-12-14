package com.example.mobilecomproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val className: String,
    val trainerName: String,
    val timeMillis: Long,
    val endTimeMillis: Long,
    val age: Int,
    val gender: String
)

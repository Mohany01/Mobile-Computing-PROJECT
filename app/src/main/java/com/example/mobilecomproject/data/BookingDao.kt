package com.example.mobilecomproject.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Query("SELECT * FROM bookings ORDER BY timeMillis ASC")
    fun getAll(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking)

    @Delete
    suspend fun delete(booking: Booking)
}
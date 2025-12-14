package com.example.mobilecomproject.data

import kotlinx.coroutines.flow.Flow

class GymRepository(
    private val bookingDao: BookingDao
) {
    fun getBookings(): Flow<List<Booking>> = bookingDao.getAll()

    suspend fun addBooking(className: String, trainer: String, timeMillis: Long, endTimeMillis: Long, age: Int, gender: String) {
        bookingDao.insert(Booking(className = className, trainerName = trainer, timeMillis = timeMillis, endTimeMillis = endTimeMillis, age = age, gender = gender))
    }

    suspend fun deleteBooking(booking: Booking) {
        bookingDao.delete(booking)
    }
}
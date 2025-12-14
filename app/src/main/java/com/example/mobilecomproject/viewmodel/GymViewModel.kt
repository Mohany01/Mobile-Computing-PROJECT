package com.example.mobilecomproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomproject.data.Booking
import com.example.mobilecomproject.data.GymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GymViewModel(
    private val repository: GymRepository
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getBookings().collect {
                _bookings.value = it
            }
        }
    }

    suspend fun addBooking(className: String, trainer: String, startTime: Long, endTime: Long, age: Int, gender: String): Boolean {
        val existingBookings = repository.getBookings().first()
        val hasConflict = existingBookings.any {
            it.className == className && (startTime < it.endTimeMillis && endTime > it.timeMillis)
        }

        return if (hasConflict) {
            false
        } else {
            viewModelScope.launch {
                repository.addBooking(className, trainer, startTime, endTime, age, gender)
            }
            true
        }
    }

    fun deleteBooking(booking: Booking) {
        viewModelScope.launch {
            repository.deleteBooking(booking)
        }
    }
}
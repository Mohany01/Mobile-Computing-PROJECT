package com.example.mobilecomproject

import com.example.mobilecomproject.data.Booking
import com.example.mobilecomproject.data.GymRepository
import com.example.mobilecomproject.viewmodel.GymViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class GymViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: GymRepository
    private lateinit var viewModel: GymViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = mock(GymRepository::class.java)

        // Mockito: when repository.getBookings() is called, return a flow with one booking
        val fakeBooking =
            Booking(id = 1, className = "Yoga", trainerName = "Ahmed", timeMillis = 0L)
        `when`(repository.getBookings()).thenReturn(flowOf(listOf(fakeBooking)))

        viewModel = GymViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsBookingsFromRepository() = runTest(dispatcher) {
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.bookings.value.size)
        assertEquals("Yoga", viewModel.bookings.value[0].className)

        verify(repository).getBookings()
    }
}
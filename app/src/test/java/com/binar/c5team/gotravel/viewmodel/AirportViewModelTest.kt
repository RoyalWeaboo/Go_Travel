package com.binar.c5team.gotravel.viewmodel

import com.binar.c5team.gotravel.model.AirportResponse
import com.binar.c5team.gotravel.model.BookingResponse
import com.binar.c5team.gotravel.model.ProfileResponse
import com.binar.c5team.gotravel.network.RestfulApi
import com.binar.c5team.gotravel.network.RestfulApiWithoutToken
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Call

class AirportViewModelTest {
    private lateinit var service: RestfulApi
    private lateinit var service2 : RestfulApiWithoutToken

    @Before
    fun setUp() {
        service = mockk()
        service2 = mockk()
    }

    @Test
    fun getAirportTest(): Unit = runBlocking {
        val respAirport = mockk<Call<AirportResponse>>()

        every {
            runBlocking {
                service2.getAirportData()
            }
        } returns respAirport

        val result = service2.getAirportData()

        verify {
            runBlocking { service2.getAirportData() }
        }
        Assert.assertEquals(result, respAirport)

    }

    @Test
    fun getProfile(): Unit = runBlocking {
        val respProfile = mockk<Call<ProfileResponse>>()

        every {
            runBlocking {
                service.getProfile()
            }
        } returns respProfile

        val result = service.getProfile()

        verify {
            runBlocking { service.getProfile() }
        }
        Assert.assertEquals(result, respProfile)

    }

    @Test
    fun getBooking(): Unit = runBlocking {
        val respBooking = mockk<Call<BookingResponse>>()

        every {
            runBlocking {
                service.getBooking()
            }
        } returns respBooking

        val result = service.getBooking()

        verify {
            runBlocking { service.getBooking() }
        }
        Assert.assertEquals(result, respBooking)

    }
}
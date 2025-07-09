package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import retrofit2.http.GET
import retrofit2.Response

interface APIreservation {

    @GET("reservations/next-reservation")
    suspend fun getNextReservation(): Response<Reservation>
}
package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query

interface APIreservation {

    @GET("reservations/next-reservation")
    suspend fun getNextReservation(): Response<Reservation>

    @GET("reservations")
    suspend fun getReservations(
        @Query("status") status : String
    ): Response<List<Reservation>>
}
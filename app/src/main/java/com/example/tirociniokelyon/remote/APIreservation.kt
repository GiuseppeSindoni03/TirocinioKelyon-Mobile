package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateReservationDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Slot
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Date

interface APIreservation {

    @GET("reservations/next-reservation")
    suspend fun getNextReservation(): Response<Reservation>

    @GET("reservations/patient")
    suspend fun getReservations(
        @Query("status") status : String = "CONFIRMED"
    ): Response<List<Reservation>>

    @GET("reservations/slots")
    suspend fun getSlots (
        @Query("date") date : String,
        @Query("visitType") visitType : String

    ) : Response<List<Slot>>


    @POST("reservations")
    suspend fun createReservation (
        @Body() createReservationDTO: CreateReservationDTO
    ) : Response<Reservation>

}
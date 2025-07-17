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


    //ti restituisce le prenotazioni future confermate, ReservationScreen
    @GET("reservations/next-reservation")
    suspend fun getNextReservations(): Response<List<Reservation>>


    // restituissce tutte le prenotazioni esistenti per uno specifico stato, ReservationScreen
    @GET("reservations/patient")
    suspend fun getReservations(
        @Query("status") status: String?
    ): Response<List<Reservation>>


    // restituisce vero se non esistono visite, AddReservationScreen
    @GET("reservations/isFirstVisit")
    suspend fun isFirstVisit(
    ): Response<Boolean>


    // restituisce gli slot per uno specifico giorno, AddReservationScreen
    @GET("reservations/slots")
    suspend fun getSlots(
        @Query("date") date: String,
        @Query("visitType") visitType: String

    ): Response<List<Slot>>


    // Aggiunge una prenotazione, Add ReservationScreen
    @POST("reservations")
    suspend fun createReservation(
        @Body() createReservationDTO: CreateReservationDTO
    ): Response<Reservation>

}
package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationStatus
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIreservation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
   private  val apiReservation: APIreservation
){

    suspend fun getNextReservation(): Result<Reservation?> {
        return try {
            val response = apiReservation.getNextReservation()

            if (response.isSuccessful) {
                val reservation = response.body()
                Result.success(reservation)
            } else {
                Result.failure(Exception("Errore HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.d("DEBUG", "Errore: $e")
            Result.failure(e)
        }
    }

    suspend fun getReservations(status: ReservationStatus): Result<List<Reservation>?> {
        return try {
            val response = apiReservation.getReservations(status.toString())

            if (response.isSuccessful) {
                val reservation = response.body()
                Result.success(reservation)
            } else {
                Result.failure(Exception("Errore HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.d("DEBUG", "Errore: $e")
            Result.failure(e)
        }
    }
}
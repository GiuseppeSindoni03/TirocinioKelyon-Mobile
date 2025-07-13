package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationStatus
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationVisitType
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateReservationDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Slot
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIreservation
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
   private  val apiReservation: APIreservation
){

    suspend fun createReservation (createReservationDTO: CreateReservationDTO ): Result<Reservation> {
        Log.d("RESERVATIONS", "Inizio create reservation Repo")

        return  try {
            val response = apiReservation.createReservation(createReservationDTO)

            if(response.isSuccessful) {
                response.body()?.let { createReservationResponse ->


                    Result.success(createReservationResponse)
                } ?: Result.failure(Exception("Empty response"))
            }
            else {
                Log.d("DEBUG", "${response.message()}")
                Result.failure(Exception("Created response Failed: ${response.message()} ${response.code()} ${response.errorBody()}"))
            }

        } catch (e: Exception) {
            Log.d("RESERVATIONS", "Errore creazione prenotazione ${e.message}")
            Result.failure(e)
        }

    }

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

        Log.d("RESERVATIONS", "Inizio chiamat repository")
        return try {
            Log.d("RESERVATIONS", "Reservation Status: ${status}")
            val response = apiReservation.getReservations(status.toString())

            Log.d("RESERVATIONS", "${response.body()}")

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

    suspend fun getSlots(visitType: ReservationVisitType, date: String): Result<List<Slot>?> {

        Log.d("RESERVATIONS", "Inizio chiamat repository get Slots")
        return try {
            val response = apiReservation.getSlots(visitType = visitType.toString(), date = date)

            Log.d("RESERVATIONS", "${response.body()}")

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
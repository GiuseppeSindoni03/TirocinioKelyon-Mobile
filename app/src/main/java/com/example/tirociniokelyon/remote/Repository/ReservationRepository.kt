package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationStatus
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationVisitType
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateReservationDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Slot
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIreservation
import okhttp3.ResponseBody
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

sealed class ReservationError {
    data class BadRequest(val message: String, val code: Int) : ReservationError()
    data class Conflict(val message: String, val code: Int) : ReservationError()
    data class NetworkError(val message: String) : ReservationError()
    data class UnknownError(val message: String) : ReservationError()
}

@Singleton
class ReservationRepository @Inject constructor(
   private  val apiReservation: APIreservation
){

    suspend fun parseErrorBody(errorBody: ResponseBody?): String {
        return try {
            errorBody?.string() ?: "Unknown error"
        } catch (e: Exception) {
            "Error parsing response"
        }
    }

    suspend fun createReservation (createReservationDTO: CreateReservationDTO ): Result<Reservation> {
        Log.d("RESERVATIONS", "Inizio create reservation Repo, $createReservationDTO")

        return  try {
            val response = apiReservation.createReservation(createReservationDTO)

            Log.d("RESERVATIONS", "Raw response: ${response.body()}")

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

    suspend fun getNextReservations(): Result<List<Reservation>?> {
        return try {
            val response = apiReservation.getNextReservations()

            if (response.isSuccessful) {
                val reservations = response.body()
                Result.success(reservations)
            } else if (response.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(Exception("Errore HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.d("DEBUG", "Errore: $e")
            Result.failure(e)
        }
    }

    suspend fun isFirstVisit(): Result<Boolean> {

        Log.d("RESERVATIONS", "Inizio chiamat repository isFirstVisit")
        return try {
            val response = apiReservation.isFirstVisit()

            Log.d("RESERVATIONS", "${response.body()}")

            if (response.isSuccessful) {
                val isFirst = response.body() ?: false
                Result.success(isFirst)
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
package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIpatient
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIreservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIuser
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepository @Inject constructor(
    private val apiUser: APIuser,
    private  val apiPatient: APIpatient,

) {

    suspend fun getMe(): Result<User> {
        return try {
            Log.d("DEBUG", "Chiamata dentro la repository get me")
            val response = apiUser.getMe()
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->


                    Result.success(loginResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DEBUG", "${response.message()}")
                Result.failure(Exception("Impossible get me: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDoctor(): Result<Doctor> {
        return try {
            Log.d("DEBUG", "Chiamata a getDoctor() iniziata")

            val response = apiPatient.getDoctor()

            Log.d("DEBUG", "Response code: ${response.code()}")
            Log.d("DEBUG", "Response message: ${response.message()}")
            Log.d("DEBUG", "Response headers: ${response.headers()}")
            Log.d("DEBUG", "Response body: ${response.body()}")
            Log.d("DEBUG", "Response raw: ${response.raw()}")

            if (response.isSuccessful) {
                response.body()?.let { doctor ->
                    Log.d("DEBUG", "Dottore ricevuto: $doctor")
                    Result.success(doctor)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Log.e("DEBUG", "Errore HTTP: ${response.code()} - ${response.message()}")
                Log.e("DEBUG", "Error body: ${response.errorBody()?.string()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DEBUG", "Eccezione durante getDoctor()", e)
            Result.failure(e)
        }
    }
}
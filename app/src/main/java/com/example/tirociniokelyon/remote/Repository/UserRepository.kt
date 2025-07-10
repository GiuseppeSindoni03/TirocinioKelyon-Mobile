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
            val response = apiPatient.getDoctor()

            if (response.isSuccessful) {
                response.body()?.let { doctor ->


                    Result.success(doctor)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DEBUG", "${response.message()}")
                Result.failure(Exception("Impossible get me: ${response.message()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
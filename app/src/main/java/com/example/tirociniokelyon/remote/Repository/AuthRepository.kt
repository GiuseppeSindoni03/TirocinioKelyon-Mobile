package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.SignInCredentialsDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIAuth
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private  val api: APIAuth,
) {
    suspend fun signIn(credentialsDTO: SignInCredentialsDTO): Result<User> {
        return try {
            Log.d("DEBUG", "Chiamata dentro la repository")
            val response = api.signIn(credentialsDTO)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->


                    Result.success(loginResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DEBUG", "${response.message()}")
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMe(): Result<User> {
        return try {
            Log.d("DEBUG", "Chiamata dentro la repository get me")
            val response = api.getMe()
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

    suspend fun logout() {
        Log.d("DEBUG", "Chiamata dentro la repository get me")
        val response = api.logout()
        if (response.isSuccessful) {
            response.body()?.let { logoutResponse ->


                Result.success(logoutResponse)
            } ?: Result.failure(Exception("Empty response"))
        } else {
            Log.d("DEBUG", "${response.message()} ${response.body()} ${response.code()}")
            Result.failure(Exception("Impossible get me: ${response.message()}"))
        }

    }


}



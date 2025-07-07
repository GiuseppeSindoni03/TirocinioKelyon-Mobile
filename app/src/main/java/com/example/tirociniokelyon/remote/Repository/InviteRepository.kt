package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.AcceptInviteRequest
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.AcceptInviteResponse
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.SignInCredentialsDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Invite
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIAuth
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIinvite
import javax.inject.Inject

class InviteRepository @Inject constructor(
    private  val api: APIinvite
) {



    suspend fun getInvite(inviteId: String): Result<Invite> {
        return try {
            Log.d("DEBUG", "Chiamata dentro la repository Invite $inviteId")
            val response = api.getInvite(inviteId)

            Log.d("DEBUG", "Raw response: ${response.raw()}")
            Log.d("DEBUG", "Response headers: ${response.headers()}")


            if (response.isSuccessful) {
                Log.d("DEBUG", "SUCCESSO REPO")
                Log.d("DEBUG", "Response body: ${response.body()?.toString()}")

                response.body()?.let { inviteResponse ->

                    Log.d("DEBUG", "Invito Repository ${response.body()}")
                    Log.d("DEBUG", "Invito Repository $inviteResponse")
                    Result.success(inviteResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DEBUG", "${response.message()}")
                Result.failure(Exception("GetInvite failed: ${response.message()}"))
            }
        } catch (e: Exception) {

                Log.e("DEBUG", "Eccezione parsing: ${Log.getStackTraceString(e)}")


            Result.failure(e)
        }
    }


    suspend fun acceptInvite(inviteId: String, userInfo: AcceptInviteRequest): Result<AcceptInviteResponse> {
        return try {
            Log.d("DEBUG", "Invio accettazione per invito $inviteId con dati: $userInfo")
            val response = api.acceptInvite(inviteId, userInfo)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("AcceptInvite failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DEBUG", "Eccezione: ${e.localizedMessage}")
            Result.failure(e)
        }
    }



}
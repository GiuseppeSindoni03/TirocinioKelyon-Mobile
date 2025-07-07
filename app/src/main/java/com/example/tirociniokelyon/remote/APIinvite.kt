package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.AcceptInviteRequest
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.AcceptInviteResponse
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Invite
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIinvite {

    @GET("invite/{id}")
    suspend fun getInvite(@Path("id") id: String): Response<Invite>

    @POST("invite/{id}/accept")
    suspend fun acceptInvite(@Path("id") id : String,
                             @Body userInfo: AcceptInviteRequest
                             ): Response<AcceptInviteResponse>


}
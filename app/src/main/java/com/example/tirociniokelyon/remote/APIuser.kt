package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import retrofit2.Response
import retrofit2.http.GET

interface APIuser {

    @GET("user/me")
    suspend fun getMe() : Response<User>

}
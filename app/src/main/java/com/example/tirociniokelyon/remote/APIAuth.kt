package com.example.tirociniokelyon.com.example.tirociniokelyon.remote
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.SignInCredentialsDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import  retrofit2.http.POST


interface APIAuth {

    @POST("auth/signin")
    suspend fun signIn(@Body() credentialsDTO: SignInCredentialsDTO) : Response<User>


    @POST("auth/logout")
    suspend fun logout() : Response<Unit>
}
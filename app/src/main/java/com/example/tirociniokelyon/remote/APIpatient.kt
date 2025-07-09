package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import retrofit2.Response
import retrofit2.http.GET

interface APIpatient {

    @GET ("patient/doctor")
    suspend fun  getDoctor (): Response<Doctor>
}
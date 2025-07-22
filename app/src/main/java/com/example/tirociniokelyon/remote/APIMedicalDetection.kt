package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateMedicalDetectionDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.MedicalDetectionDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.MedicalDetectionsDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface APIMedicalDetection {

    @GET("medical-detection")
    suspend fun getMedicalDetection(
        @Query("type") type: String,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ) : Response<MedicalDetectionsDTO>


    @GET("medical-detection/last")
    suspend fun getLastDetection(
        @Query("type") type: String
    ) : Response<MedicalDetectionDTO>

    @POST("medical-detection")
    suspend fun postMedicalDetection (
        @Body() medicalDetection: CreateMedicalDetectionDTO
    ): Response<MedicalDetection>


}
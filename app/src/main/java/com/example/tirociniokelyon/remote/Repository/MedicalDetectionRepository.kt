package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateMedicalDetectionDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.MedicalDetectionDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.MedicalDetectionsDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIMedicalDetection
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MedicalDetectionRepository @Inject constructor(
    private val api: APIMedicalDetection
) {

    suspend fun getLastMedicalDetection(
        type: String
    ): Result<MedicalDetection> {
        return try {
            Log.d("DETECTION", "Chimaata getLastMedicalDetection Type: $type")

            val response = api.getLastDetection(type)

            Log.d("DETECTION", "Response ricevuta, isSuccessful: ${response.isSuccessful}")


            if (response.isSuccessful) {
                val body = response.body()
                Log.d("DETECTION", "Response body: $body")
                body?.let { detection ->
                    Log.d("DETECTION", "Detection trovata: ${detection.detection}")

                    Result.success(detection.detection)
                } ?: run {  Log.e("DETECTION", "Response body è null"); Result.failure(Exception("Empty response"));

                }
            } else {
                Log.e("DETECTION", "Response non successful: ${response.message()}, code: ${response.code()}")
                Result.failure(Exception("Created response Failed: ${response.message()} ${response.code()} ${response.errorBody()}"))
            }
        } catch (e: Exception) {
            Log.e("DETECTION", "Errore getLastDetection: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getMedicalDetections(
        type: String,
        startDate: String?,
        endDate: String?
    ): Result<MedicalDetectionsDTO> {
        Log.d("DETECTION", "Inizio getLastDetection Repo")

        return try {
            val response = api.getMedicalDetection(type, startDate, endDate)


            if (response.isSuccessful) {
                response.body()?.let { response ->


                    Result.success(response)
                } ?: Result.failure(Exception("Empty response"))
            } else {

                when (response.code()) {
                    400 -> {
                        Log.d("DETECTION", "Bad request: ${response.message()}")
                        Result.failure(Exception("Bad request: ${response.message()}"))
                    }
                    404 -> {
                        // Return success with empty detections list for 404
                        Log.d("DETECTION", "No detections found (404)")
                        Result.success(MedicalDetectionsDTO(detections = emptyList(), total = 0))
                    }
                    else -> {
                        Log.d("DETECTION", "${response.message()}")
                        Result.failure(Exception("Request failed: ${response.message()} ${response.code()}"))
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("DETECTION", "Errore get detection ${e.message}")
            Result.failure(e)
        }
    }


    suspend fun postMedicalDetection(
        medicalDetection: CreateMedicalDetectionDTO
    ): Result<MedicalDetection> {
        Log.d("DETECTION", "Inizio crea medicalDetection repo")

        return try {
            val response = api.postMedicalDetection(medicalDetection)

            if (response.isSuccessful) {
                response.body()?.let { detection ->


                    Result.success(detection)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DETECTION", "${response.message()}")
                Result.failure(Exception("Created response Failed: ${response.message()} ${response.code()} ${response.errorBody()}"))
            }
        } catch (e: Exception) {
            Log.d("DETECTION", "Errore creazione detection ${e.message}")
            Result.failure(e)
        }
    }

}
package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateMedicalDetectionDTO
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
            Log.d("DETECTION", "Chimata getLastMedicalDetection")

            val response = api.getLastDetection(type)

            if (response.isSuccessful) {
                response.body()?.let { detection ->


                    Result.success(detection)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DETECTION", "${response.message()}")
                Result.failure(Exception("Created response Failed: ${response.message()} ${response.code()} ${response.errorBody()}"))
            }
        } catch (e: Exception) {
            Log.d("DETECTION", "Errore getLastDetection ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getMedicalDetections(
        type: String,
        startDate: String?,
        endDate: String?
    ): Result<List<MedicalDetection>> {
        Log.d("DETECTION", "Inizio getLastDetection Repo")

        return try {
            val response = api.getMedicalDetection(type, startDate, endDate)


            if (response.isSuccessful) {
                response.body()?.let { detections ->


                    Result.success(detections)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Log.d("DETECTION", "${response.message()}")
                Result.failure(Exception("Created response Failed: ${response.message()} ${response.code()} ${response.errorBody()}"))
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
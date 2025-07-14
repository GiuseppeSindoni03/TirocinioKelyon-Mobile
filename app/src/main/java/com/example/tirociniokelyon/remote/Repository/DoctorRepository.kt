package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

import android.util.Log
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIpatient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoctorRepository @Inject constructor(
    private val apiPatient: APIpatient
) {
    private var cachedDoctor: Doctor? = null
    private var isLoading = false
    private val mutex = Mutex()

    suspend fun getDoctor(): Result<Doctor> {
        return mutex.withLock {
            // Se abbiamo già il dottore in cache, restituiscilo
            cachedDoctor?.let {
                Log.d("DoctorRepository", "Dottore restituito dalla cache")
                return Result.success(it)
            }

            // Se stiamo già caricando, aspetta
            if (isLoading) {
                Log.d("DoctorRepository", "Caricamento già in corso, aspetto...")
                while (isLoading) {
                    kotlinx.coroutines.delay(100)
                }
                return cachedDoctor?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Errore durante il caricamento"))
            }

            // Carica il dottore
            isLoading = true
            Log.d("DoctorRepository", "Caricamento dottore da API...")

            try {
                val response = apiPatient.getDoctor()

                Log.d("DoctorRepository", "Response code: ${response.code()}")
                Log.d("DoctorRepository", "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.let { doctor ->
                        Log.d("DoctorRepository", "Dottore caricato e salvato in cache: $doctor")
                        cachedDoctor = doctor
                        isLoading = false
                        Result.success(doctor)
                    } ?: run {
                        isLoading = false
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Log.e("DoctorRepository", "Errore HTTP: ${response.code()} - ${response.message()}")
                    isLoading = false
                    Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Log.e("DoctorRepository", "Eccezione durante getDoctor()", e)
                isLoading = false
                Result.failure(e)
            }
        }
    }

    // Metodo per invalidare la cache (utile per logout o refresh)
    fun clearCache() {
        cachedDoctor = null
        isLoading = false
        Log.d("DoctorRepository", "Cache del dottore pulita")
    }

    // Metodo per verificare se il dottore è in cache
    fun hasCachedDoctor(): Boolean {
        return cachedDoctor != null
    }
}
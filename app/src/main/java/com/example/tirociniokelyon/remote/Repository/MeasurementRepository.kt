package com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository

//import com.example.tirociniokelyon.com.example.tirociniokelyon.model.SpO2Result
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIMeasurement
import javax.inject.Inject

class MeasurementRepository @Inject constructor(private val api: APIMeasurement) {

//    suspend fun sendSpO2(spo2: SpO2Result): Boolean {
//        return try {
//            val res = api.sendMeasurement( spo2)
//
//            res.isSuccessful
//
//        } catch (e: Exception) {
//            false
//        }
//    }
}

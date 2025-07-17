package com.example.tirociniokelyon.com.example.tirociniokelyon.model

data class DeviceMeasurement(
    val spo2: Int? = null,           // Saturazione ossigeno
    val heartRate: Int? = null,      // Frequenza cardiaca
    val sys: Int? = null,            // Pressione sistolica
    val dia: Int? = null,            // Pressione diastolica
    val temperature: Float? = null   // Temperatura corporea
)
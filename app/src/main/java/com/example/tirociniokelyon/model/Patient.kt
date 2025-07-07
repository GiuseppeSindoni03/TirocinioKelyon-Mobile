package com.example.tirociniokelyon.com.example.tirociniokelyon.model

data class Patient(
    val id: String,
    val weight: Float,
    val height: Float,
    val bloodType: String,
    val level: String,
    val sport: String,
    val pathologies: List<String>,
    val medications: List<String>,
    val injuries: List<String>,


)


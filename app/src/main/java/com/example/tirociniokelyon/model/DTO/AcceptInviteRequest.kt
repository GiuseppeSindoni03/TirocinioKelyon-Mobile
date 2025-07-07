package com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO

data class AcceptInviteRequest(
    val name: String,
    val surname: String,
    val email: String,
    val cf: String,
    val birthDate: String, // Assicurati che sia in ISO format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val gender: String,
    val phone: String,
    val address: String,
    val city: String,
    val cap: String,
    val province: String,
    val password: String
)

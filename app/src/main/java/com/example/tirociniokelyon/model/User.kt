package com.example.tirociniokelyon.com.example.tirociniokelyon.model




data class User(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val birthDate: String,
    val cf: String,
    val gender: String,
    val phone: String,
    val role: String,
    val address: String,
    val city: String,
    val cap: String,
    val province: String,
    val patient : Patient,
)


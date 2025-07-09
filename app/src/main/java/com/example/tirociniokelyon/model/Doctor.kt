package com.example.tirociniokelyon.com.example.tirociniokelyon.model


data class Doctor(
    val userId: String,
    val specialization: String?,
    val medicalOffice: String?,
    val registrationNumber: String?,
    val orderProvince: String?,
    val orderDate: String?,
    val orderType: String?,
    val user: UserDoctor

)

data class UserDoctor (
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val birthDate: String,
    val phone: String,
    val cf: String,
    val gender: String,
    val role: String,
    val address: String,
    val city: String,
    val cap: String,
    val province: String,
)
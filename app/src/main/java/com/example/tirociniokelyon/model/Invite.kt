package com.example.tirociniokelyon.com.example.tirociniokelyon.model

import java.util.Date


data class Invite(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val cf: String,
    val birthDate: String,
    val gender: String,
    val phone: String,
    val address: String,
    val city: String,
    val cap: String,
    val province: String,
    val weight: Float,
    val height: Float,
    val bloodType: String,
    val level: String,
    val sport: String,
    val pathologies: List<String>,
    val medications: List<String>,
    val injuries: List<String>,
    val used: Boolean,
    val createdAt: String,
    val doctor: Doctor?,   // optional
    val patient: Patient?  // optional
)

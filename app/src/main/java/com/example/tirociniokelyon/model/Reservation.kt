package com.example.tirociniokelyon.com.example.tirociniokelyon.model

import java.util.Date

data class Reservation (
    val id: String,
    val status: String,
    val createAt: Date,
    val startDate: Date,
    val endDate: Date,
    val visitType: String
    )
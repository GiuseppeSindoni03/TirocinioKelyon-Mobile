package com.example.tirociniokelyon.com.example.tirociniokelyon.model

import java.util.Date

data class Reservation (
    val id: String,
    val status: String,
    val createAt: Date,
    val startTime: Date,
    val endTime: Date,
    val visitType: String
    )


data class Slot (
    val startTime: Date,
    val endTime: Date
)
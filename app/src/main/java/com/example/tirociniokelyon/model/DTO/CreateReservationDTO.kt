package com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO

import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationVisitType
import java.util.Date

data class CreateReservationDTO(
    val startTime: String,
    val endTime: String,
    val visitType: String
)

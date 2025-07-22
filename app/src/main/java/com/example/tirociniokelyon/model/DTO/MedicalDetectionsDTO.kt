package com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection

data class MedicalDetectionsDTO(
    val detections: List<MedicalDetection>,
    val total: Number
)

package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.MedicalDetectionCard
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection

@Composable
fun LastMedicalDetections (
    spo2: MedicalDetection?,
    temp: MedicalDetection?,
    hr: MedicalDetection?,
    weight: MedicalDetection?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Ultime rilevazioni",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MedicalDetectionCard(
                detection = spo2,
                type = "SPO2",
                unit = " %",
                icon = Icons.Outlined.Favorite,
                color = Color.Red,
                onAddClick = {}
            )

            MedicalDetectionCard(
                detection = hr,
                type = "HR",
                unit = " bpm",
                icon = Icons.Outlined.MonitorHeart,
                color = Color.Red.copy(alpha = 0.7f), onAddClick = {}
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            MedicalDetectionCard(
                detection = temp,
                type = "Temperature",
                unit = " °C",
                icon = Icons.Outlined.DeviceThermostat,
                color = MaterialTheme.colorScheme.primary,
                onAddClick = {}
            )

            MedicalDetectionCard(
                detection = weight,
                type = "Peso",
                unit = " kg",
                icon = Icons.Outlined.MonitorWeight,
                color = Color(0xff00c853),
                onAddClick = {}
            )

        }
    }
}
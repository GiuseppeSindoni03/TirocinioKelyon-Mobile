package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.util.Log
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ShortcutCard
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.MedicalDetectionViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection


@Composable
fun MedicalDetectionScreen(navController: NavController) {

    val viewModel: MedicalDetectionViewModel = hiltViewModel()
    val medicalDetectionsState by viewModel.medicalDetectionsState.collectAsState()
    val lastDetectionState by viewModel.lastDetectionState.collectAsState()

    LaunchedEffect(lastDetectionState) {
        Log.d("MedicalDetection", "lastDetectionState: ${lastDetectionState}")
        Log.d("MedicalDetection", "lastSPO2: ${lastDetectionState.lastSPO2}")
        Log.d("MedicalDetection", "lastHR: ${lastDetectionState.lastHR}")
    }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {
                Text(
                    text = "Rilevazione mediche",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
            }
        },


        bottomBar = {
            NavBar(navController = navController)
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                when {
                    lastDetectionState.isLoading ->
                        LoadingComponent()


                    lastDetectionState.error != null ->
                        ErrorComponent(error = lastDetectionState.error.toString())

                    else -> {

                        Column (
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ){
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
                                    detection = lastDetectionState.lastSPO2,
                                    type = "SPO2",
                                    unit = " %",
                                    icon = Icons.Outlined.Favorite,
                                    color = Color.Red,
                                    onAddClick = {}
                                )

                                MedicalDetectionCard(
                                    detection = lastDetectionState.lastHR,
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
                                    detection = lastDetectionState.lastTemperature,
                                    type = "Temperature",
                                    unit = " °C",
                                    icon = Icons.Outlined.DeviceThermostat,
                                    color = MaterialTheme.colorScheme.primary,
                                    onAddClick = {}
                                )

                                MedicalDetectionCard(
                                    detection = lastDetectionState.lastWeight,
                                    type = "Peso",
                                    unit = " Kg",
                                    icon = Icons.Outlined.MonitorWeight,
                                    color = Color( 0xff00c853),
                                    onAddClick = {}
                                )

                            }
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun MedicalDetectionCard(
    detection: MedicalDetection?,
    type: String,
    unit: String,
    icon: ImageVector,
    color: Color,
    onAddClick: () -> Unit
) {
    when (detection) {
        null -> {
            // Stato vuoto: mostra card per aggiungere misurazione
            ShortcutCard(
                subtitle = type,
                icon = icon,
                title = "Nessuna rilevazione",
                colorIcon = color.copy(alpha = 0.3f),
                onClick = onAddClick
            )
        }

        else -> {
            // Ha dati: mostra la card normale
            ShortcutCard(
                subtitle = type,
                icon = icon,
                title = detection.value.toString() + unit,
                colorIcon = color,
                onClick = onAddClick
            )
        }
    }
}
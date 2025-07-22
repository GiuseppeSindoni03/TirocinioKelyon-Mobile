package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LastMedicalDetections
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MedicalDetectionForm
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MedicalDetectionsHeader
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MedicalDetectionsList
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ShortcutCard
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.MedicalDetectionViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicalDetectionScreen(navController: NavController) {

    val viewModel: MedicalDetectionViewModel = hiltViewModel()
    val medicalDetectionsState by viewModel.medicalDetectionsState.collectAsState()
    val lastDetectionState by viewModel.lastDetectionState.collectAsState()

    var isManualDialogOpen by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )



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
            NavBar(navController = navController , openFormModal = {isManualDialogOpen = true}, )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                when {
                    lastDetectionState.isLoading ->
                        LoadingComponent()


                    lastDetectionState.error != null ->
                        ErrorComponent(error = lastDetectionState.error.toString())

                    else -> {

                        LastMedicalDetections(
                            spo2 = lastDetectionState.lastSPO2,
                            temp = lastDetectionState.lastTemperature,
                            hr = lastDetectionState.lastHR,
                            weight = lastDetectionState.lastWeight
                        )


                    }

                }

                when {
                    medicalDetectionsState.isLoading -> {
                        LoadingComponent()
                    }

                    medicalDetectionsState.error != null -> {
                        ErrorComponent(error = lastDetectionState.error.toString())
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {

                            MedicalDetectionsHeader(
                                onTypeChange = { type -> viewModel.updateCurrentType(type) },
                                onViewChange = { view -> viewModel.updateCurrentView(view) },

                                currentType = medicalDetectionsState.currentType.toString(),
                                currentView = medicalDetectionsState.currentView.toString(),
                            )


                            Spacer(modifier = Modifier.height(16.dp))
                            MedicalDetectionsList(
                                detections = medicalDetectionsState.medicalDetections,
                                currentType = medicalDetectionsState.currentType.toString(),
                            )
                        }
                    }
                }

                if(isManualDialogOpen) {
                    MedicalDetectionForm(onDismiss =  { isManualDialogOpen = false} )

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
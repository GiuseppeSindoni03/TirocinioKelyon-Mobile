package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.SpO2ViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Save

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ConnectedDeviceCard
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.DeviceSelectionModal
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.EmptyAnimation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MedicalDetection
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MedicalDetectionCompleted
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import com.linktop.whealthService.OnBLEService


@Composable
fun SpO2Screen(
    context: Context,
    navController: NavController,
    viewModel: SpO2ViewModel = viewModel()
) {
    val context = LocalContext.current

    val errorMessage by viewModel.errorMessage.collectAsState()


    // Stato Bluetooth (dal manager singleton)
    val bluetoothManager = remember { BluetoothManagerSingleton.getInstance() }
    val isBluetoothReady by bluetoothManager.isReady.collectAsState()
    val bluetoothStatus by bluetoothManager.initializationStatus.collectAsState()

    val spO2Value by viewModel.spO2.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isMeasuring by viewModel.isMeasuring.collectAsState()
    val deviceList by viewModel.deviceList.collectAsState()
    val measurementCompleted by viewModel.measurementCompleted.collectAsState()

    val connectedDevice by viewModel.connectedDevice.collectAsState() // Aggiungi questo nel ViewModel


    var isScanning by remember { mutableStateOf(false) }




    LaunchedEffect(isScanning) {
        Log.d("UI", "Stato scanning: $isScanning")
    }


    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Errore") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Rilevazione medica",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black
                )
//                Text(
//                    text = "Stato: $bluetoothStatus",
//                    style = MaterialTheme.typography.bodySmall
//                )
            }
        },
        bottomBar = {
            Column {
//                if(isConnected) {
                Button(
                    onClick = {
                        if (isMeasuring) viewModel.stopMeasurement()
                        else viewModel.startMeasurement()
                    },
                    shape = RoundedCornerShape(16.dp),
                    enabled = isBluetoothReady && isConnected,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (isMeasuring) "Ferma Misurazione" else "Inizia Misurazione",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

//                }
                NavBar(navController = navController)
            }

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
//                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (isConnected && connectedDevice != null) {
                ConnectedDeviceCard(
                    context = context,
                    device = connectedDevice!!,
                    onDisconnect = { viewModel.disconnect() }
                )
            }

            if (!isConnected) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(text = "Stato Bluetooth", style = MaterialTheme.typography.titleMedium)

                        Text(
                            if (isBluetoothReady) "Bluetooth abilitato ✅" else "Bluetooth non abilitato ❌",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Stato connessione",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            if (isConnected) "Connesso ✅" else "Disconnesso ❌",
                            style = MaterialTheme.typography.bodyLarge
                        )



                        if (!isConnected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    try {

                                        // Avvia la scansione
                                        viewModel.startScan()
                                        isScanning = true


//                                    if (deviceList.isEmpty()) viewModel.startScan()
//                                    else viewModel.stopScan()

                                    } catch (e: Exception) {
                                        Log.e("UI", "❌ Crash nel click: ${e.message}", e)
                                    }
                                },
                                enabled = isBluetoothReady,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Avvia Scansione")
                            }
                        }
                    }
                }


            } else {


                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        if (isMeasuring) {
                            MedicalDetection(spO2Value = spO2Value, heartRate = heartRate)
                        }
                        else if (measurementCompleted && spO2Value > 0 && heartRate > 0) {
                                // Mostra i risultati della misurazione con il pulsante Salva
                                MedicalDetectionCompleted(spO2Value = spO2Value, heartRate = heartRate, onSaveDetection = { viewModel.saveMeasurement() })


                            } else {
                                Text(
                                    text = "Nessuna rilevazione al momento...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                    }

            }
        }
    }

    if (isScanning) {
        DeviceSelectionModal(
            deviceList = deviceList,
            onDeviceSelected = { device ->
                viewModel.connectToDevice(device)
                isScanning = false
            },
            onDismiss = {
                isScanning = false
                viewModel.stopScan()
            }

        )
    }
}


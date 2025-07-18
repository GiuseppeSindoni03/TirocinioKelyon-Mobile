package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.SpO2ViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState

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
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.EmptyAnimation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import com.linktop.whealthService.OnBLEService


@Composable
fun SpO2Screen(
    activity: ComponentActivity,
    navController: NavController,
    viewModel: SpO2ViewModel = viewModel()
) {
    val context = LocalContext.current

    val errorMessage by viewModel.errorMessage.collectAsState()


    // Stato Bluetooth (dal manager singleton)
    val bluetoothManager = remember { BluetoothManagerSingleton.getInstance() }
    val isBluetoothReady by bluetoothManager.isReady.collectAsState()
    val bluetoothStatus by bluetoothManager.initializationStatus.collectAsState()

    // Stato ViewModel
    val spO2Value by viewModel.spO2.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isMeasuring by viewModel.isMeasuring.collectAsState()
    val deviceList by viewModel.deviceList.collectAsState()

    // RIMUOVIAMO la doppia inizializzazione - il BluetoothManager è già stato inizializzato
    // nella MainActivity


//    // Inizializza BluetoothManager una volta sola
//    LaunchedEffect(Unit) {
//        bluetoothManager.initialize(activity)
//    }

//    LaunchedEffect(Unit) {
//        try {
//            bluetoothManager.initialize(activity)
//        } catch (e: Exception) {
//            Log.e("SpO2Screen", "Initialization error", e)
//            // Mostra un messaggio all'utente
//        }
//    }

    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError()},
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
                Text(
                    text = "Stato: $bluetoothStatus",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        bottomBar = {
            NavBar(navController = navController)
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
            // ✅ Stato connessione
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bluetooth abilitato: ${if (isBluetoothReady) "Sì" else "No"}")
                    Text("Connessione: ${if (isConnected) "Connesso" else "Disconnesso"}")

                    val hasPermissions = PermissionUtils.hasBluetoothPermissions(context)
                    Text(
                        text = "Permessi: ${if (hasPermissions) "OK" else "Mancanti"}",
                        color = if (hasPermissions) Color.Green else Color.Red
                    )

                    if (!isConnected) {
                        Button(
                            onClick = {
                                try {
                                    if (deviceList.isEmpty()) viewModel.startScan()
                                    else viewModel.stopScan()
                                } catch (e: Exception) {
                                    Log.e("UI", "❌ Crash nel click: ${e.message}", e)
                                }
                            },
                            enabled = isBluetoothReady
                        ) {
                            Text(if (deviceList.isEmpty()) "Avvia Scansione" else "Ferma Scansione")
                        }
                    }
                }
            }

            // ✅ Lista dispositivi
            if (!isConnected) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Dispositivi trovati: ${deviceList.size}")
                        if (deviceList.isEmpty()) {
                            EmptyAnimation("Nessun dispositivo rilevato")
                        } else {
                            LazyColumn {
                                items(deviceList) { device ->
                                    DeviceListItem(
                                        device = device,
                                        isConnected = false,
                                        hasBluetoothPermission = true
                                    ) {
                                        viewModel.connectToDevice(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ✅ Rilevazione attiva
            if (isConnected) {
                Button(
                    onClick = {
                        if (isMeasuring) viewModel.stopMeasurement()
                        else viewModel.startMeasurement()
                    },
                    enabled = isBluetoothReady
                ) {
                    Text(if (isMeasuring) "Ferma Misurazione" else "Inizia Misurazione")
                }

                if (isMeasuring) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("SpO₂: $spO2Value%", style = MaterialTheme.typography.headlineSmall)
                            Text("HR: $heartRate bpm", style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DeviceListItem(
    device: OnBLEService.DeviceSort,
    isConnected: Boolean,
    hasBluetoothPermission: Boolean,
    onDeviceClick: (OnBLEService.DeviceSort) -> Unit
) {
    val context = LocalContext.current

    Log.d("DEVICE", "Dispositivo collegato $device")

    val deviceName = remember(device, hasBluetoothPermission) {
        if (hasBluetoothPermission) {
            try {
                device.bleDevice.name ?: "Dispositivo sconosciuto"
            } catch (e: SecurityException) {
                "Dispositivo sconosciuto"
            }
        } else {
            "Dispositivo sconosciuto"
        }
    }
    val deviceAddress = remember(device, hasBluetoothPermission) {
        if (hasBluetoothPermission) {
            try {
                device.bleDevice.address ?: "Indirizzo non disponibile"
            } catch (e: SecurityException) {
                "Indirizzo non disponibile"
            }
        } else {
            "Indirizzo non disponibile"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (!isConnected) {
                onDeviceClick(device)
            }
        },
        enabled = !isConnected && hasBluetoothPermission,
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = deviceAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (device.rssi != null) {
                    Text(
                        text = "RSSI: ${device.rssi} dBm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


        }
    }
}


private fun getDeviceName(context: Context, device: OnBLEService.DeviceSort): String {
    return try {
        if (PermissionUtils.hasBluetoothPermissions(context)) {
            device.bleDevice.name ?: "Nome sconosciuto"
        } else {
            "Permessi necessari"
        }
    } catch (e: SecurityException) {
        "Permessi insufficienti"
    } catch (e: Exception) {
        "Nome sconosciuto"
    }
}

private fun getDeviceAddress(context: Context, device: OnBLEService.DeviceSort): String {
    return try {
        if (PermissionUtils.hasBluetoothPermissions(context)) {
            device.bleDevice.address ?: "Indirizzo non disponibile"
        } else {
            "Permessi necessari"
        }
    } catch (e: SecurityException) {
        "Permessi insufficienti"
    } catch (e: Exception) {
        "Indirizzo non disponibile"
    }
}
package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.SpO2ViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
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
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.linktop.whealthService.OnBLEService
import kotlinx.coroutines.delay

@Composable
fun SpO2Screen(
    activity: ComponentActivity,
    navController: NavController,
    viewModel: SpO2ViewModel = viewModel()
) {
    val context = LocalContext.current

    // Collect degli stati dal ViewModel
    val spO2Value by viewModel.spO2Value.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isMeasuring by viewModel.isMeasuring.collectAsState()
    val fingerDetected by viewModel.fingerDetected.collectAsState()
    val deviceList by viewModel.deviceList.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    // Stati del BluetoothManager
    val isBluetoothReady by viewModel.isBluetoothReady.collectAsState()
    val bluetoothStatus by viewModel.bluetoothStatus.collectAsState()

    val deviceConnected by viewModel.deviceConnected.collectAsState()


    LaunchedEffect(isConnected) {
        Log.d("SpO2Screen", "🔍 Stato connessione cambiato nella UI: $isConnected")
    }


    LaunchedEffect(Unit) {
        val hasPermissions = PermissionUtils.hasBluetoothPermissions(context)
        Log.d("SpO2Screen", "Permessi Bluetooth: $hasPermissions")

        // Verifica GPS
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d("SpO2Screen", "GPS abilitato: $isGpsEnabled")

        // Avvia il test di connessione se tutto è pronto
        if (hasPermissions && isGpsEnabled) {
            viewModel.startConnectionTest()
        }
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
                    text = "Rilevazione medica",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
            }
        },
        bottomBar = {
//            Column(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Button(
//                        onClick = { viewModel.startScan() },
//                        enabled = isBluetoothReady && !isConnected && !isScanning,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Scansiona")
//                    }
//
//                    Button(
//                        onClick = { viewModel.stopScan() },
//                        enabled = isBluetoothReady && isScanning,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Ferma Scan")
//                    }
//                }
//                NavBar(navController = navController)
//            }


            NavBar(navController = navController)

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stato del servizio Bluetooth
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isBluetoothReady)
                        Color.White
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {


                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
//                    Text(
//                        text = "Stato Bluetooth: $bluetoothStatus",
//                        style = MaterialTheme.typography.titleMedium
//                    )
                    Column {
                        Text(
                            text = "Stato connessione",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (isConnected) "Connesso" else "Disconnesso",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )

                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if(!isConnected) {
                        Button(
                            onClick = {
                                if (isScanning) {
                                    viewModel.stopScan()
                                } else {
                                    viewModel.startScan()
                                }
                            },
                            enabled = isBluetoothReady && !isConnected,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(if (isScanning) "Ferma Scansione" else "Avvia Scansione")
                        }
                    }



                }
            }

            if (!isConnected) {
                // Lista dispositivi trovati
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Dispositivi trovati (${deviceList.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )

                        if(isScanning) {
                            EmptyAnimation("Scansione in corso")

                        } else  if (deviceList.isNotEmpty()) {

                            LazyColumn(
                                modifier = Modifier.heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(deviceList) { device ->
//                                    DeviceListItem(
//                                        device = device,
//                                        isConnected = isConnected,
//                                        hasBluetoothPermission = true,
//                                        onDeviceClick =  { (isConnected) -> {
////                                                viewModel.connectToDevice(device)
////
//                                        }
//
//                                        }
//                                    )
                                    Card(
                                        onClick = {
                                            if (!isConnected) {
                                                viewModel.connectToDevice(device)
                                            }
                                        },
                                        enabled = !isConnected && isBluetoothReady,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Text(
                                                text = getDeviceName(context, device),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = getDeviceAddress(context, device),
                                                style = MaterialTheme.typography.bodySmall
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
                                    Spacer(modifier = Modifier.height(8.dp))


                                }
                            }

                        } else {
                            EmptyAnimation("Nessun dispositivo rilevato")
                        }

                        Spacer(modifier = Modifier.height(8.dp))


                        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween){
                            Button(
                                onClick = { viewModel.refreshDeviceList() },
                                enabled = isBluetoothReady,
//                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Aggiorna Lista Dispositivi")
                            }

                            Button(
                                onClick = { viewModel.connectToFirstDevice() },
                                enabled = isBluetoothReady && !isConnected && deviceList.isNotEmpty(),
//                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Connetti Primo")
                            }
                        }


                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }
            } else {
                Card (
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

                    colors = CardDefaults.cardColors(
                            Color.White)
                ) {

                    Column (modifier = Modifier.fillMaxWidth()
                    ){
                        if(deviceConnected != null) {
                            Log.d("Misurazione", deviceConnected.toString())
                            DeviceListItem(
                                device = deviceConnected!!,
                                isConnected = false,
                                hasBluetoothPermission = true,
                                onDeviceClick = {}
                            )
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.disconnectDevice() },
                            enabled = isBluetoothReady && isConnected,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Disconnetti")
                        }
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))

                
                // Controlli di misurazione
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Button(
//                        onClick = { viewModel.startMeasurement() },
//                        enabled = isBluetoothReady && isConnected && !isMeasuring,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Inizia Misurazione")
//                    }
//
//                    Button(
//                        onClick = { viewModel.stopMeasurement() },
//                        enabled = isBluetoothReady && isMeasuring,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Ferma Misurazione")
//                    }
//                }
                
                Button(
                    onClick = {
                        if (isMeasuring) {
                            viewModel.stopMeasurement()
                        } else {
                            viewModel.startMeasurement()
                        }
                    },
                    enabled = isBluetoothReady && isConnected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isMeasuring) "Ferma Misurazione" else "Inizia Misurazione")
                }
                
                if(isMeasuring) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMeasuring)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Log.d("Misurazione", "Stato Misurazione: ${if (isMeasuring) "In corso..." else "Ferma"}")
                            Text(
                                text = "Stato Misurazione: ${if (isMeasuring) "In corso..." else "Ferma"}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Dito rilevato: ${if (fingerDetected) "Sì" else "No"}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(8.dp))

                            // Valori misurati
                            Log.d("Misurazione", "SpO₂: $spO2Value %")
                            if (spO2Value > 0 || heartRate > 0) {
                                Text(
                                    text = "SpO₂: $spO2Value %",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (spO2Value >= 95)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )


                                Text(
                                    text = "Frequenza cardiaca: $heartRate bpm",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (heartRate in 60..100)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    text = "Nessuna misurazione ricevuta",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                } else
                {
                    Text(text = "Clicca su inizia rilevazione")
                    Spacer(modifier = Modifier.height(16.dp))

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

            when {
                !hasBluetoothPermission -> {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Permessi richiesti",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                isConnected -> {
                    Icon(
                        Icons.Default.Block,
                        contentDescription = "Non disponibile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Connetti",
                        tint = MaterialTheme.colorScheme.primary
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
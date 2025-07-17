package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.SpO2ViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.linktop.whealthService.OnBLEService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    navController: NavController,
    viewModel: SpO2ViewModel = viewModel()
) {
    val context = LocalContext.current

    // Collect degli stati dal ViewModel
    val devices by viewModel.deviceList.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isBluetoothReady by viewModel.isBluetoothReady.collectAsState()
    val bluetoothStatus by viewModel.bluetoothStatus.collectAsState()

    var hasBluetoothPermission by remember { mutableStateOf(false) }
    var batteryLevel by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        hasBluetoothPermission = PermissionUtils.hasBluetoothPermissions(context)
    }

    // Aggiorna il livello batteria periodicamente
    LaunchedEffect(isConnected) {
        if (isConnected) {
            while (isConnected) {
                batteryLevel = viewModel.getBatteryLevel()
                kotlinx.coroutines.delay(5000) // Aggiorna ogni 5 secondi
            }
        } else {
            batteryLevel = -1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispositivi BLE") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isScanning) {
                                viewModel.stopScan()
                            } else {
                                viewModel.startScan()
                            }
                        },
                        enabled = isBluetoothReady && !isConnected
                    ) {
                        Icon(
                            if (isScanning) Icons.Default.Stop else Icons.Default.Search,
                            contentDescription = if (isScanning) "Ferma scansione" else "Avvia scansione"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isConnected) {
                FloatingActionButton(
                    onClick = {
                        viewModel.disconnectDevice()
                    },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(Icons.Default.BluetoothDisabled, contentDescription = "Disconnetti")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Stato del Bluetooth
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isBluetoothReady)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Stato Bluetooth: $bluetoothStatus",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Permessi: ${if (hasBluetoothPermission) "Concessi" else "Necessari"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Stato della connessione
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isConnected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isConnected) "Dispositivo Connesso" else "Nessun Dispositivo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isConnected && batteryLevel >= 0) {
                            Text(
                                text = "Batteria: $batteryLevel%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Icon(
                        if (isConnected) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Stato scansione
            if (isScanning) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Scansione in corso...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Controlli per la scansione
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.startScan() },
                    enabled = isBluetoothReady && !isConnected && !isScanning,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Scansiona")
                }

                Button(
                    onClick = { viewModel.connectToFirstDevice() },
                    enabled = isBluetoothReady && !isConnected && devices.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Connetti Primo")
                }

                Button(
                    onClick = { viewModel.refreshDeviceList() },
                    enabled = isBluetoothReady,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aggiorna")
                }
            }

            // Lista dispositivi
            if (devices.isEmpty() && !isScanning) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BluetoothSearching,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nessun dispositivo trovato",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Premi 'Scansiona' per cercare dispositivi BLE",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(devices) { device ->
                        DeviceListItem(
                            device = device,
                            isConnected = isConnected,
                            onDeviceClick = { selectedDevice ->
                                if (!isConnected) {
                                    viewModel.connectToDevice(selectedDevice)
                                }
                            },
                            hasBluetoothPermission = hasBluetoothPermission
                        )
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
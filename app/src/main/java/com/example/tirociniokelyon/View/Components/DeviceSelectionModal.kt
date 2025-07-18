package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.linktop.whealthService.OnBLEService


@Composable
fun DeviceSelectionModal(
    deviceList: List<OnBLEService.DeviceSort>,
    onDeviceSelected: (OnBLEService.DeviceSort) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header del modal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleziona Dispositivo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Chiudi")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contatore dispositivi e pulsante rescan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row {
                        if(deviceList.isNotEmpty()) {
                            Text(
                                text = "Dispositivi trovati: ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(text = "${deviceList.size}", style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }

                    }



                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista dispositivi
                if (deviceList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyAnimation("Nessun dispositivo rilevato")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), // Altezza fissa per il modal
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(deviceList) { device ->
                            DeviceListItem(
                                device = device,
                                isConnected = false,
                                hasBluetoothPermission = true,
                                onDeviceClick = onDeviceSelected
                            )
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
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            MyImage(100.dp)
            
            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {


                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
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


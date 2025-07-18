package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.linktop.whealthService.OnBLEService


@Composable
fun ConnectedDeviceCard(
    context: Context,
    device: OnBLEService.DeviceSort,
    onDisconnect: () -> Unit
) {
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
//                .height(300.dp)
                .padding(16.dp)
        ) {

            Text(text = "Dispositivo connesso", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MyImage(150.dp)


                Column(modifier = Modifier.padding(top = 20.dp)) {

                    Text(
                        text = "Nome:",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = getDeviceName(context, device),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Indirizzo:",
                        style = MaterialTheme.typography.bodyLarge,

                    )
                    Text(
                        text = device.bleDevice.address ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black

                    )
                    Spacer(modifier = Modifier.height(4.dp))


                    Text(
                        text = "Segnale: ",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "${device.rssi} dBm",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black

                    )
                }


            }

            Button (onClick = onDisconnect, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Text(text = "Disconnetti")
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







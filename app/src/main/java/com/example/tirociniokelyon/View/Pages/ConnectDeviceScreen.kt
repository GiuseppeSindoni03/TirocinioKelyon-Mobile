//package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import com.example.tirociniokelyon.utils.BleConnector
//import com.linktop.whealthService.OnBLEService
//
//@Composable
//fun ConnectDeviceScreen(
//    bleManager: BleConnector
//) {
//    val context = LocalContext.current
//    val devices = remember { mutableStateListOf<OnBLEService.DeviceSort>() }
//
//    Column {
//        Button(onClick = {
//            bleManager.scanDevices(true)
//            devices.clear()
//            devices.addAll(bleManager.getDeviceList())
//        }) {
//            Text("Cerca dispositivi")
//        }
//
//        LazyColumn {
//            items(devices) { device ->
//                Text(text = device.bleDevice.name ?: "Sconosciuto")
//                Button(onClick = {
//                    bleManager.connectToDevice(device.bleDevice)
//                }) {
//                    Text("Connetti")
//                }
//            }
//        }
//    }
//}

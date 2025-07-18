package com.example.tirociniokelyon.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.linktop.MonitorDataTransmissionManager
import com.linktop.constant.BluetoothState
import com.linktop.infs.OnBleConnectListener
import com.linktop.infs.OnSpO2ResultListener
import com.linktop.whealthService.MeasureType
import com.linktop.whealthService.OnBLEService

class BleConnector(private val context: Context) {

    private val manager: MonitorDataTransmissionManager =
        MonitorDataTransmissionManager.getInstance()


    var isServiceReady = false
        private set

    var isConnected = false
        private set

    // Callback da assegnare dal ViewModel
    var onConnectionChanged: ((Boolean) -> Unit)? = null
    var onSpO2DataReceived: ((spo2: Int, heartRate: Int) -> Unit)? = null
    var onDeviceListUpdated: ((List<OnBLEService.DeviceSort>) -> Unit)? = null
    var onMeasurementCompleted: ((Boolean) -> Unit)? = null


    init {
        Log.d("BleConnector", "🔧 Inizializzazione BleConnector...")
        bindService()
    }

    private fun bindService() {
        manager.bind(
            com.linktop.DeviceType.HealthMonitor,
            context,
            object : MonitorDataTransmissionManager.OnServiceBindListener {
                override fun onServiceBind() {
                    Log.d("BleConnector", "✅ Servizio BLE collegato")
                    isServiceReady = true
                    setupListeners()
                }

                override fun onServiceUnbind() {
                    Log.d("BleConnector", "🔌 Servizio BLE scollegato")
                    isServiceReady = false
                    isConnected = false
                    onConnectionChanged?.invoke(false)
                }
            }
        )
    }

    private fun setupListeners() {
        manager.setOnBleConnectListener(object : OnBleConnectListener {
            override fun onBleState(state: Int) {
                when (state) {
                    BluetoothState.BLE_CONNECTED_DEVICE -> {
                        isConnected = true
                        Log.d("BleConnector", "✅ Dispositivo connesso")
                        onConnectionChanged?.invoke(true)
                    }

                    BluetoothState.BLE_OPENED_AND_DISCONNECT,
                    BluetoothState.BLE_CLOSED -> {
                        isConnected = false
                        Log.d("BleConnector", "🔌 Dispositivo disconnesso")
                        onConnectionChanged?.invoke(false)
                    }
                }
            }

            override fun onOpenBLE() {
                Log.d("BleConnector", "📶 Bluetooth attivato")
            }

            override fun onBLENoSupported() {
                Log.e("BleConnector", "❌ BLE non supportato")
            }

            override fun onUpdateDialogBleList() {
                onDeviceListUpdated?.invoke(getAvailableDevices())
            }
        })

        manager.setOnSpO2ResultListener(object : OnSpO2ResultListener {
            override fun onSpO2Result(spo2: Int, pr: Int) {
                Log.d("BleConnector", "📊 SpO2: $spo2%, HR: $pr bpm")
                onSpO2DataReceived?.invoke(spo2, pr)

                val isValidMeasurement = spo2 > 0 && spo2 <= 100 && pr > 0 && pr <= 250
                if (isValidMeasurement) {
                    onMeasurementCompleted?.invoke(true)
                }
            }

            override fun onSpO2Wave(p0: Int) {} // Ignorato per semplicità

            override fun onSpO2End() {
                Log.d("BleConnector", "🛑 Misurazione SpO₂ terminata")
            }

            override fun onFingerDetection(detected: Int) {
                Log.d("BleConnector", "👉 Dito rilevato: ${detected == 1}")
            }
        })
    }

    fun startScan() {

        Log.d("BleConnector", "🔍 startScan() chiamato")
        if (isServiceReady) {
            try {
                Log.d("BleConnector", "🔍 Avvio scansione BLE")
                manager.autoScan(true)
                Log.d("BleConnector", "✅ Scansione avviata con successo")
            } catch (e: Exception) {
                Log.e("BleConnector", "❌ Errore in startScan()", e)
                throw e
            }
        } else {
            Log.w("BleConnector", "⚠️ Servizio non pronto, scansione non avviata")
        }

    }

    fun stopScan() {
        if (isServiceReady) {
            Log.d("BleConnector", "⏹️ Stop scansione BLE")
            manager.autoScan(false)
        }
    }

    fun getAvailableDevices(): List<OnBLEService.DeviceSort> {
        return manager.deviceList ?: emptyList()
    }

    fun connectToDevice(device: OnBLEService.DeviceSort) {
        if (isServiceReady) {
            Log.d("BleConnector", "🔗 Connessione a ${device.bleDevice}")
            manager.connectToBle(device.bleDevice)
        }
    }

    fun disconnect() {
        if (isServiceReady) {
            Log.d("BleConnector", "🔌 Disconnessione dispositivo")
            manager.disConnectBle()
        }
    }

    fun startSpO2Measurement() {
        if (isServiceReady && isConnected) {
            Log.d("BleConnector", "🫁 Avvio misurazione SpO₂")
            manager.startMeasure(MeasureType.SPO2, false)
        }
    }

    fun stopSpO2Measurement() {
        if (isServiceReady) {
            Log.d("BleConnector", "🛑 Stop misurazione SpO₂")
            manager.stopMeasure(MeasureType.SPO2)
        }
    }

    fun cleanup() {
        if (isServiceReady) {
            manager.unBind()
            isServiceReady = false
        }
    }
}

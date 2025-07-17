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
    private var isServiceBound = false
    private var isMeasurementInProgress = false
    private var measurementStartTime = 0L


    var onConnectionStateChanged: ((Boolean) -> Unit)? = null
    var onSpO2DataReceived: ((spo2: Int, hr: Int) -> Unit)? = null
    var onDeviceListUpdated: ((List<OnBLEService.DeviceSort>) -> Unit)? = null

    var onServiceReady: (() -> Unit)? = null


    init {
        Log.d("BLE_DEBUG", "🚀 Inizializzazione BleConnector")
        initializeManager()
    }

    private fun initializeManager() {
        manager.bind(
            com.linktop.DeviceType.HealthMonitor,
            context,
            object : MonitorDataTransmissionManager.OnServiceBindListener {
                override fun onServiceBind() {
                    Log.d("BLE", "✅ Servizio BLE collegato")
                    isServiceBound = true

                    // Verifica stato manager
                    Log.d("BLE_DEBUG", "📊 Stato manager dopo bind:")
                    Log.d("BLE_DEBUG", "  - isConnected: ${manager.isConnected}")
                    Log.d("BLE_DEBUG", "  - isScanning: ${manager.isScanning}")
                    Log.d("BLE_DEBUG", "  - deviceList size: ${manager.deviceList?.size ?: 0}")
                    setupListeners()

                    onServiceReady?.invoke()
                }

                override fun onServiceUnbind() {
                    Log.d("BLE", "❌ Servizio BLE scollegato")
                    isServiceBound = false
                    isMeasurementInProgress = false
                    onConnectionStateChanged?.invoke(false)

                }
            })
    }


    private fun setupListeners() {
        Log.d("BLE_DEBUG", "🔧 Configurazione listener BLE")

        manager.setOnBleConnectListener(object : OnBleConnectListener {
            override fun onBleState(state: Int) {
                Log.d("BLE_DEBUG", "📡 Stato BLE cambiato: $state")
                when (state) {
                    BluetoothState.BLE_CONNECTED_DEVICE -> {
                        Log.d("BLE_DEBUG", "✅ CONNESSO al dispositivo")
                        Log.d("BLE_DEBUG", "  - Manager isConnected: ${manager.isConnected}")
                        Log.d("BLE_DEBUG", "  - Batteria: ${manager.batteryValue}%")

                        onConnectionStateChanged?.invoke(true)
                    }

                    BluetoothState.BLE_OPENED_AND_DISCONNECT -> {
                        Log.d("BLE", "❌ Disconnesso dal dispositivo")
                        isMeasurementInProgress = false

                        onConnectionStateChanged?.invoke(false)

                    }

                    BluetoothState.BLE_CLOSED -> {
                        Log.d("BLE", "❌ Bluetooth spento")
                        isMeasurementInProgress = false

                        onConnectionStateChanged?.invoke(false)
                    }
                }
            }

            override fun onOpenBLE() {
                Log.d("BLE", "🔄 Bluetooth deve essere attivato")
            }

            override fun onBLENoSupported() {
                Log.e("BLE", "🚫 BLE non supportato dal dispositivo")
            }

            override fun onUpdateDialogBleList() {
                Log.d("BLE", "📋 Lista dispositivi aggiornata")
                updateDeviceList()
            }
        })



        manager.setOnSpO2ResultListener(object : OnSpO2ResultListener {
            override fun onSpO2Result(spo2: Int, pr: Int) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = if (measurementStartTime > 0) {
                    (currentTime - measurementStartTime) / 1000.0
                } else 0.0

                Log.d("SPO2_DEBUG", "📊 RISULTATO SpO₂ ricevuto:")
                Log.d("SPO2_DEBUG", "  - SpO₂: $spo2%")
                Log.d("SPO2_DEBUG", "  - Heart Rate: $pr bpm")
                Log.d("SPO2_DEBUG", "  - Tempo trascorso: ${elapsedTime}s")
                Log.d("SPO2_DEBUG", "  - Misurazione in corso: $isMeasurementInProgress")

                // Validazione dati
                if (spo2 in 70..100 && pr in 40..200) {
                    Log.d("SPO2_DEBUG", "✅ Dati validi ricevuti")
                    onSpO2DataReceived?.invoke(spo2, pr)
                } else {
                    Log.w("SPO2_DEBUG", "⚠️ Dati fuori range normale - SpO₂: $spo2%, HR: $pr bpm")
                    onSpO2DataReceived?.invoke(spo2, pr) // Invia comunque, ma logga l'anomalia
                }

            }

            override fun onSpO2Wave(p0: Int) {
                Log.d("SPO2_DEBUG", "⏹️ FINE misurazione SpO₂")
                Log.d(
                    "SPO2_DEBUG",
                    "  - Durata totale: ${(System.currentTimeMillis() - measurementStartTime) / 1000.0}s"
                )
                isMeasurementInProgress = false
                measurementStartTime = 0L
            }

            override fun onSpO2End() {
                Log.d("SPO2", "⏹️ Misurazione SpO₂ terminata")
            }

            override fun onFingerDetection(detected: Int) {
                Log.d("SPO2_DEBUG", "👆 Rilevamento dito:")
                Log.d("SPO2_DEBUG", "  - Valore raw: $detected")
                Log.d(
                    "SPO2_DEBUG",
                    "  - Interpretazione: ${if (detected == 1) "DITO RILEVATO" else "NESSUN DITO"}"
                )

                if (detected == 1 && isMeasurementInProgress) {
                    Log.d("SPO2_DEBUG", "✅ Dito rilevato durante misurazione - tutto OK")
                } else if (detected != 1 && isMeasurementInProgress) {
                    Log.w("SPO2_DEBUG", "⚠️ Dito non rilevato durante misurazione!")
                }
            }
        })
    }




    fun isServiceReady(): Boolean {
        return isServiceBound
    }

    // Metodo per configurare listener SpO2 in modo sicuro
    fun setSpO2Listener(listener: OnSpO2ResultListener) {
        if (isServiceBound) {
            manager.setOnSpO2ResultListener(listener)
        } else {
            Log.e("BLE", "❌ Servizio non pronto per configurare listener SpO2")
        }
    }

    fun getAvailableDevices(): List<OnBLEService.DeviceSort> {
        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return emptyList()
        }

        val deviceList = manager.deviceList
        return if (deviceList != null) {
            Log.d("BLE", "📱 Restituendo ${deviceList.size} dispositivi disponibili")

            // Controlla i permessi prima di accedere a name e address
            val hasBluetoothPermission = ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            deviceList.forEach { device ->
                val deviceName = if (hasBluetoothPermission) {
                    try {
                        device.bleDevice.name ?: "Nome sconosciuto"
                    } catch (e: SecurityException) {
                        "Nome sconosciuto"
                    }
                } else {
                    "Nome sconosciuto"
                }

                val deviceAddress = if (hasBluetoothPermission) {
                    try {
                        device.bleDevice.address ?: "Indirizzo non disponibile"
                    } catch (e: SecurityException) {
                        "Indirizzo non disponibile"
                    }
                } else {
                    "Indirizzo non disponibile"
                }

                Log.d("BLE", "  - $deviceName ($deviceAddress)")
            }
            deviceList
        } else {
            Log.w("BLE", "⚠️ Lista dispositivi è null")
            emptyList()
        }
    }

    private fun updateDeviceList() {
        if (isServiceBound) {
            val deviceList = manager.deviceList
            deviceList?.let { devices ->
                Log.d("BLE", "📱 Trovati ${devices.size} dispositivi")
                onDeviceListUpdated?.invoke(devices)
            }
        }
    }

    fun startScan() {
        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return
        }
        Log.d("BLE", "🔍 Avvio scansione BLE")
        manager.autoScan(true)
    }

    fun stopScan() {
        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return
        }
        Log.d("BLE", "⏹️ Stop scansione BLE")
        manager.autoScan(false)
    }

    fun connectToDevice(device: OnBLEService.DeviceSort) {
        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BLE", "❗ Permesso BLUETOOTH_CONNECT non concesso")
            return
        }

        Log.d("BLE", "🔗 Connessione a ${device.bleDevice.name} - ${device.bleDevice.address}")
        manager.connectToBle(device.bleDevice)
    }

    fun connectToFirstAvailable() {

        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return
        }

        val deviceList = manager.deviceList
        if (deviceList.isNotEmpty()) {
            val device = deviceList[0].bleDevice

            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("BLE", "❗ Permesso BLUETOOTH_CONNECT non concesso")
                return
            }

            Log.d("BLE", "🔗 Connessione a ${device.name} - ${device.address}")
            manager.connectToBle(device)
        } else {
            Log.w("BLE", "⚠️ Nessun dispositivo BLE trovato")
        }
    }


    fun disconnect() {
//        manager.disConnectBle()
        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return
        }
        Log.d("BLE", "🔌 Disconnessione dal dispositivo")
        manager.disConnectBle()
    }

    fun startSpO2Measurement() {
        Log.d("SPO2_DEBUG", "🫁 === INIZIO MISURAZIONE SpO₂ ===")

        if (!isServiceBound) {
            Log.e("BLE", "❌ Servizio non collegato")
            return
        }
        if (!manager.isConnected) {
            Log.e("BLE", "❌ Nessun dispositivo connesso")
            Log.d("SPO2_DEBUG", "  - Manager isConnected: ${manager.isConnected}")
            return
        }

        if (isMeasurementInProgress) {
            Log.w("SPO2_DEBUG", "⚠️ Misurazione già in corso, fermando quella precedente")
            stopSpO2Measurement()
            Thread.sleep(1000) // Aspetta un secondo prima di riavviare


        }

        Log.d("SPO2_DEBUG", "📋 Stato pre-misurazione:")
        Log.d("SPO2_DEBUG", "  - Servizio collegato: $isServiceBound")
        Log.d("SPO2_DEBUG", "  - Dispositivo connesso: ${manager.isConnected}")
        Log.d("SPO2_DEBUG", "  - Batteria: ${manager.batteryValue}%")
        Log.d("SPO2_DEBUG", "  - Scansione in corso: ${manager.isScanning}")

//        Log.d("BLE", "🫁 Avvio misurazione SpO₂")
//        Log.d("BLE", "📊 Stato servizio: bound=$isServiceBound, connected=${manager.isConnected}")
//
//        manager.startMeasure(MeasureType.SPO2, false)

        measurementStartTime = System.currentTimeMillis()
        isMeasurementInProgress = true

        Log.d("SPO2_DEBUG", "🚀 Avvio misurazione SpO₂...")
        try {
            manager.startMeasure(MeasureType.SPO2, false)
            Log.d("SPO2_DEBUG", "✅ Comando startMeasure inviato con successo")

            // Programma un controllo dopo 5 secondi
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                checkMeasurementProgress()
            }, 5000)

        } catch (e: Exception) {
            Log.e("SPO2_DEBUG", "💥 ERRORE durante avvio misurazione: ${e.message}")
            isMeasurementInProgress = false
            measurementStartTime = 0L
        }

    }

    fun testDeviceResponse() {
        Log.d("BLE_DEBUG", "🧪 === TEST RISPOSTA DISPOSITIVO ===")

        if (!isServiceBound) {
            Log.e("BLE_DEBUG", "❌ Servizio non collegato")
            return
        }

        if (!manager.isConnected) {
            Log.e("BLE_DEBUG", "❌ Dispositivo non connesso")
            return
        }

        Log.d("BLE_DEBUG", "📊 Informazioni dispositivo:")
        try {
            val battery = manager.batteryValue
            Log.d("BLE_DEBUG", "  - Batteria: $battery%")

            val deviceList = manager.deviceList
            Log.d("BLE_DEBUG", "  - Dispositivi in lista: ${deviceList?.size ?: 0}")

            Log.d("BLE_DEBUG", "  - Manager isConnected: ${manager.isConnected}")
            Log.d("BLE_DEBUG", "  - Manager isScanning: ${manager.isScanning}")

        } catch (e: Exception) {
            Log.e("BLE_DEBUG", "💥 ERRORE durante test: ${e.message}")
        }
    }

    private fun checkMeasurementProgress() {
        Log.d("SPO2_DEBUG", "🔍 Controllo progresso misurazione:")
        Log.d("SPO2_DEBUG", "  - Misurazione in corso: $isMeasurementInProgress")
        Log.d(
            "SPO2_DEBUG",
            "  - Tempo trascorso: ${(System.currentTimeMillis() - measurementStartTime) / 1000.0}s"
        )
        Log.d("SPO2_DEBUG", "  - Dispositivo ancora connesso: ${manager.isConnected}")

        if (isMeasurementInProgress && measurementStartTime > 0) {
            val elapsedTime = (System.currentTimeMillis() - measurementStartTime) / 1000.0
            if (elapsedTime > 30) { // Se sono passati più di 30 secondi
                Log.w("SPO2_DEBUG", "⚠️ Misurazione troppo lunga, possibile problema")
            }
        }
    }

    //    fun stopSpO2Measurement() {
//        if (!isServiceBound) {
//            Log.e("BLE", "❌ Servizio non collegato")
//            return
//        }
//
//        Log.d("BLE", "⏹️ Stop misurazione SpO₂")
//        manager.stopMeasure(MeasureType.SPO2)
//    }
    fun stopSpO2Measurement() {
        Log.d("SPO2_DEBUG", "⏹️ === STOP MISURAZIONE SpO₂ ===")

        if (!isServiceBound) {
            Log.e("SPO2_DEBUG", "❌ Servizio non collegato")
            return
        }

        if (!isMeasurementInProgress) {
            Log.w("SPO2_DEBUG", "⚠️ Nessuna misurazione in corso")
        }

        try {
            manager.stopMeasure(MeasureType.SPO2)
            Log.d("SPO2_DEBUG", "✅ Comando stopMeasure inviato")
        } catch (e: Exception) {
            Log.e("SPO2_DEBUG", "💥 ERRORE durante stop misurazione: ${e.message}")
        } finally {
            isMeasurementInProgress = false
            measurementStartTime = 0L
        }

    }

    fun isConnected(): Boolean {
        return isServiceBound && manager.isConnected
    }

    fun isScanning(): Boolean {
        return isServiceBound && manager.isScanning
    }

    fun getBatteryLevel(): Int {
        return if (isServiceBound) {
            manager.batteryValue
        } else {
            -1
        }
    }

    fun cleanup() {
        if (isServiceBound) {
            manager.unBind()
        }
    }
}

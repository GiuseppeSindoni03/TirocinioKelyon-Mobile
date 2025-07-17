package com.example.tirociniokelyon.com.example.tirociniokelyon.utils


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.tirociniokelyon.utils.BleConnector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BluetoothManagerSingleton private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: BluetoothManagerSingleton? = null

        fun getInstance(): BluetoothManagerSingleton {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BluetoothManagerSingleton().also { INSTANCE = it }
            }
        }
    }

    private var bleConnector: BleConnector? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var context: Context? = null

    // Stati osservabili
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private val _hasPermissions = MutableStateFlow(false)
    val hasPermissions: StateFlow<Boolean> = _hasPermissions

    private val _initializationStatus = MutableStateFlow<String>("Non inizializzato")
    val initializationStatus: StateFlow<String> = _initializationStatus

    // Launchers per permessi e bluetooth
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var bluetoothEnableLauncher: ActivityResultLauncher<Intent>? = null

    /**
     * Inizializza il BluetoothManager con il contesto dell'activity
     */
    fun initialize(activity: ComponentActivity) {
        Log.d("BluetoothManager", "🚀 Inizializzazione BluetoothManager")

        this.context = activity

        // Configura i launchers
        setupLaunchers(activity)

        // Inizializza l'adapter Bluetooth
        val bluetoothManager =
            activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Log.e("BluetoothManager", "❌ Bluetooth non supportato")
            _initializationStatus.value = "Bluetooth non supportato"
            Toast.makeText(activity, "Bluetooth non supportato", Toast.LENGTH_LONG).show()
            return
        }

        _initializationStatus.value = "Verifica permessi..."

        // Verifica permessi
        checkPermissions()
    }

    private fun setupLaunchers(activity: ComponentActivity) {
        // Launcher per i permessi
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            _hasPermissions.value = allGranted

            if (allGranted) {
                Log.d("BluetoothManager", "✅ Tutti i permessi concessi")
                _initializationStatus.value = "Permessi ottenuti"
                checkBluetoothEnabled()
            } else {
                Log.e("BluetoothManager", "❌ Permessi negati: ${permissions.filter { !it.value }}")
                _initializationStatus.value = "Permessi negati"
                Toast.makeText(activity, "Permessi Bluetooth necessari", Toast.LENGTH_LONG).show()
            }
        }

        // Launcher per abilitare il Bluetooth
        bluetoothEnableLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("BluetoothManager", "✅ Bluetooth abilitato")
                _isBluetoothEnabled.value = true
                _initializationStatus.value = "Bluetooth abilitato"
                initializeBleConnector()
            } else {
                Log.e("BluetoothManager", "❌ Bluetooth non abilitato")
                _initializationStatus.value = "Bluetooth non abilitato"
                Toast.makeText(context, "Bluetooth necessario", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermissions() {
        context?.let { ctx ->
            if (PermissionUtils.hasAllBlePermissions(ctx)) {
                Log.d("BluetoothManager", "✅ Permessi già concessi")
                _hasPermissions.value = true
                checkBluetoothEnabled()
            } else {
                Log.d("BluetoothManager", "🔐 Richiesta permessi")
                _initializationStatus.value = "Richiesta permessi..."
                val missingPermissions = PermissionUtils.getMissingBluetoothPermissions(ctx) +
                        PermissionUtils.getMissingLocationPermissions(ctx)

                permissionLauncher?.launch(missingPermissions.distinct().toTypedArray())
            }
        }
    }

    private fun checkBluetoothEnabled() {
        if (bluetoothAdapter?.isEnabled == false) {
            Log.d("BluetoothManager", "🔄 Richiesta abilitazione Bluetooth")
            _initializationStatus.value = "Richiesta abilitazione Bluetooth..."
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothEnableLauncher?.launch(enableBtIntent)
        } else {
            Log.d("BluetoothManager", "✅ Bluetooth già abilitato")
            _isBluetoothEnabled.value = true
            initializeBleConnector()
        }
    }

    private fun initializeBleConnector() {
        context?.let { ctx ->
            Log.d("BluetoothManager", "🔗 Inizializzazione BleConnector")
            _initializationStatus.value = "Inizializzazione BLE..."

            bleConnector = BleConnector(ctx).apply {
                onServiceReady = {
                    Log.d("BluetoothManager", "✅ BLE Service pronto")
                    _isInitialized.value = true
                    _initializationStatus.value = "BLE pronto"
                }

                onConnectionStateChanged = { connected ->
                    Log.d("BluetoothManager", "🔗 Stato connessione: $connected")
                    // Questi log aiuteranno a tracciare la connessione
                }

                onDeviceListUpdated = { devices ->
                    Log.d("BluetoothManager", "📱 Dispositivi trovati: ${devices.size}")
                    devices.forEachIndexed { index, device ->
                        Log.d("BluetoothManager", "  $index: ${getDeviceName(device)}")
                    }
                }
            }
        }
    }

    private fun getDeviceName(device: com.linktop.whealthService.OnBLEService.DeviceSort): String {
        return try {
            context?.let { ctx ->
                if (PermissionUtils.hasPermission(
                        ctx,
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    )
                ) {
//                    device.bleDevice.name ?:
                    "Nome sconosciuto"
                } else {
                    "Permesso mancante"
                }
            } ?: "Contesto non disponibile"
        } catch (e: Exception) {
            "Errore: ${e.message}"
        }
    }

    /**
     * Ottiene il BleConnector (solo se inizializzato)
     */
    fun getBleConnector(): BleConnector? {
        if (!_isInitialized.value) {
            Log.w("BluetoothManager", "⚠️ BleConnector non ancora inizializzato")
            return null
        }
        return bleConnector
    }

    /**
     * Avvia test di connessione con logging dettagliato
     */
    fun startConnectionTest() {
        Log.d("BluetoothManager", "🧪 === INIZIO TEST CONNESSIONE ===")

        val connector = getBleConnector()
        if (connector == null) {
            Log.e("BluetoothManager", "❌ BleConnector non disponibile")
            return
        }

        Log.d("BluetoothManager", "🔍 Avvio scansione dispositivi...")
        connector.startScan()

        // Dopo 10 secondi, controlla i dispositivi trovati
//        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
//            val devices = connector.getAvailableDevices()
//            Log.d("BluetoothManager", "📋 Dispositivi disponibili: ${devices.size}")
//
//            if (devices.isNotEmpty()) {
//                Log.d("BluetoothManager", "🎯 Tentativo connessione al primo dispositivo...")
//                connector.connectToFirstAvailable()
//            } else {
//                Log.w("BluetoothManager", "⚠️ Nessun dispositivo trovato")
//            }
//        }, 10000)

        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val connectionChecker = object : Runnable {
            override fun run() {
                val isConnected = connector.isConnected()
                Log.d("BluetoothManager", "🔄 Controllo connessione: $isConnected")
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(connectionChecker, 2000)

        // Dopo 10 secondi, controlla i dispositivi trovati
        handler.postDelayed({
            val devices = connector.getAvailableDevices()
            Log.d("BluetoothManager", "📋 Dispositivi disponibili: ${devices.size}")

            if (devices.isNotEmpty()) {
                Log.d("BluetoothManager", "🎯 Tentativo connessione al primo dispositivo...")
                connector.connectToFirstAvailable()

                // Verifica connessione dopo 5 secondi
                handler.postDelayed({
                    val isConnected = connector.isConnected()
                    Log.d("BluetoothManager", "🔍 Verifica connessione post-tentativo: $isConnected")
                }, 5000)
            } else {
                Log.w("BluetoothManager", "⚠️ Nessun dispositivo trovato")
            }
        }, 10000)
    }

    /**
     * Pulisce le risorse
     */
    fun cleanup() {
        Log.d("BluetoothManager", "🧹 Pulizia BluetoothManager")
        bleConnector?.cleanup()
        bleConnector = null
        context = null
        _isInitialized.value = false
        _initializationStatus.value = "Ripulito"
    }

    /**
     * Verifica se tutto è pronto per le operazioni BLE
     */
    fun isReady(): Boolean {
        return _isInitialized.value && _isBluetoothEnabled.value && _hasPermissions.value
    }
}
package com.example.tirociniokelyon.utils

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
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
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

    private var context: Context? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val _hasPermissions = MutableStateFlow(false)
    val hasPermissions: StateFlow<Boolean> = _hasPermissions

    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private val _initializationStatus = MutableStateFlow("Non inizializzato")
    val initializationStatus: StateFlow<String> = _initializationStatus

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    private var currentActivity: ComponentActivity? = null

    fun initialize(activity: ComponentActivity) {
        try {
            context = activity.applicationContext // Usa applicationContext per evitare memory leak
            currentActivity = activity

            val btManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                ?: throw IllegalStateException("BluetoothManager non disponibile")

            bluetoothAdapter = btManager.adapter ?: throw IllegalStateException("Bluetooth non supportato")

            checkInitialState()
        } catch (e: Exception) {
            Log.e("BluetoothManager", "Errore inizializzazione", e)
            _initializationStatus.value = "Errore: ${e.localizedMessage}"
            Toast.makeText(activity, "Errore Bluetooth: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkInitialState() {
        context?.let { ctx ->
            val hasAll = PermissionUtils.hasAllBlePermissions(ctx)
            _hasPermissions.value = hasAll

            val bluetoothEnabled = bluetoothAdapter?.isEnabled == true
            _isBluetoothEnabled.value = bluetoothEnabled

            updateReadyState()

            if (hasAll && bluetoothEnabled) {
                _initializationStatus.value = "Bluetooth pronto"
            } else {
                _initializationStatus.value = "Configurazione necessaria"
            }
        }
    }

    // Metodo per richiedere permessi (da chiamare dall'Activity)

    fun requestPermissions(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        context?.let { ctx ->
            val missing = PermissionUtils.getMissingBluetoothPermissions(ctx) +
                    PermissionUtils.getMissingLocationPermissions(ctx)

            if (missing.isNotEmpty()) {
                _initializationStatus.value = "Richiesta permessi..."
                permissionLauncher.launch(missing.distinct().toTypedArray())
            } else {
                _hasPermissions.value = true
                updateReadyState()
            }
        }
    }

    // Metodo per richiedere attivazione Bluetooth (da chiamare dall'Activity)
    fun requestBluetoothEnable(bluetoothLauncher: ActivityResultLauncher<Intent>) {
        if (bluetoothAdapter?.isEnabled == false) {
            _initializationStatus.value = "Richiesta attivazione Bluetooth..."
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothLauncher.launch(enableIntent)
        } else {
            _isBluetoothEnabled.value = true
            updateReadyState()
        }
    }


    // Callback per gestire il risultato dei permessi
    fun onPermissionsResult(permissions: Map<String, Boolean>) {
        val allGranted = permissions.all { it.value }
        _hasPermissions.value = allGranted

        if (allGranted) {
            Log.d("BluetoothManager", "✅ Tutti i permessi concessi")
            updateReadyState()
        } else {
            val denied = permissions.filter { !it.value }.keys.joinToString()
            Log.e("BluetoothManager", "❌ Permessi negati: $denied")
            _initializationStatus.value = "Permessi negati: $denied"
            Toast.makeText(context, "Alcuni permessi sono necessari", Toast.LENGTH_LONG).show()
        }
    }


    // Callback per gestire il risultato dell'attivazione Bluetooth
    fun onBluetoothEnableResult(isEnabled: Boolean) {
        if (isEnabled) {
            Log.d("BluetoothManager", "✅ Bluetooth abilitato")
            _isBluetoothEnabled.value = true
            updateReadyState()
        } else {
            Log.e("BluetoothManager", "❌ Bluetooth non abilitato")
            _initializationStatus.value = "Bluetooth non abilitato"
            Toast.makeText(context, "Bluetooth richiesto", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateReadyState() {
        val ready = _hasPermissions.value && _isBluetoothEnabled.value
        _isReady.value = ready
        _initializationStatus.value = if (ready) "Bluetooth pronto" else "Non pronto"

        if (ready) {
            Log.d("BluetoothManager", "✅ BluetoothManager pronto per l'uso")
        }
    }

    fun cleanup() {
        Log.d("BluetoothManager", "🧹 Cleanup BluetoothManager")
        context = null
        currentActivity = null
        _isReady.value = false
        _initializationStatus.value = "Ripulito"
    }
}

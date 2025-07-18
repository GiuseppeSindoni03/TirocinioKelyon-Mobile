package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Device
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.example.tirociniokelyon.utils.BleConnector
import com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import com.linktop.infs.OnSpO2ResultListener
import com.linktop.whealthService.OnBLEService
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SpO2ViewModel(application: Application) : AndroidViewModel(application) {

    private val bleConnector = BleConnector(application)

    // Esposti alla UI
    private val _deviceList = MutableStateFlow<List<OnBLEService.DeviceSort>>(emptyList())
    val deviceList: StateFlow<List<OnBLEService.DeviceSort>> = _deviceList.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isMeasuring = MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean> = _isMeasuring.asStateFlow()

    private val _spO2 = MutableStateFlow(0)
    val spO2: StateFlow<Int> = _spO2.asStateFlow()

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _connectedDevice = MutableStateFlow<OnBLEService.DeviceSort?>(null)
    val connectedDevice: StateFlow<OnBLEService.DeviceSort?> = _connectedDevice.asStateFlow()

    private val _measurementCompleted = MutableStateFlow(false)
    val measurementCompleted: StateFlow<Boolean> = _measurementCompleted.asStateFlow()

    init {
        Log.d("SpO2ViewModel", "🔧 ViewModel inizializzato")

        setupBleConnectorCallbacks()

    }

    private fun setupBleConnectorCallbacks() {
        try {
            // Callback per ricevere aggiornamenti da BleConnector
            bleConnector.onConnectionChanged = { connected ->
                viewModelScope.launch {
                    _isConnected.value = connected
                    if (!connected) {
                        _isMeasuring.value = false
                    }
                    Log.d("SpO2ViewModel", "🔌 Stato connessione: $connected")
                }
            }

            bleConnector.onDeviceListUpdated = { devices ->
                viewModelScope.launch {
                    _deviceList.value = devices
                    Log.d("SpO2ViewModel", "📡 Trovati ${devices.size} dispositivi")
                }
            }

            bleConnector.onSpO2DataReceived = { spo2, hr ->
                viewModelScope.launch {
                    _spO2.value = spo2
                    _heartRate.value = hr
                    Log.d("SpO2ViewModel", "📊 SpO₂: $spo2%, HR: $hr bpm")
                }
            }

            bleConnector.onMeasurementCompleted = { completed ->
                viewModelScope.launch {
                    _measurementCompleted.value = completed
                    Log.d("SpO2ViewModel", "✅ Misurazione completata: $completed")
                }
            }

        } catch (e: Exception) {
            Log.e("SpO2ViewModel", "Errore nell'inizializzazione dei callback", e)
            _errorMessage.value = "Errore nell'inizializzazione: ${e.message}"
        }
    }

    fun startScan() {
        Log.d("SpO2ViewModel", "🔍 isServiceReady = ${bleConnector.isServiceReady}")

        viewModelScope.launch {
            try {
                Log.d("SpO2ViewModel", "🔍 Avvio scansione...")

                // Verifica permessi prima di iniziare la scansione
                if (!PermissionUtils.hasBluetoothPermissions(getApplication())) {
                    _errorMessage.value = "Permessi Bluetooth necessari"
                    Log.e("SpO2ViewModel", "Permessi Bluetooth mancanti")
                    return@launch
                }

                // Verifica se il Bluetooth è abilitato
                val bluetoothManager = BluetoothManagerSingleton.getInstance()
                if (!bluetoothManager.isReady.value) {
                    _errorMessage.value = "Bluetooth non abilitato"
                    Log.e("SpO2ViewModel", "Bluetooth non abilitato")
                    return@launch
                }



                // Pulisci la lista precedente
                _deviceList.value = emptyList()

                // Avvia la scansione
                bleConnector.startScan()

            } catch (e: Exception) {
                Log.e("SpO2ViewModel", "Errore durante l'avvio della scansione", e)
                _errorMessage.value = "Errore durante la scansione: ${e.message}"
            }
        }
    }

    fun stopScan() {
        viewModelScope.launch {
            try {
                Log.d("SpO2ViewModel", "🛑 Arresto scansione...")
                bleConnector.stopScan()
            } catch (e: Exception) {
                Log.e("SpO2ViewModel", "Errore durante l'arresto della scansione", e)
                _errorMessage.value = "Errore durante l'arresto: ${e.message}"
            }
        }
    }

    fun connectToDevice(device: OnBLEService.DeviceSort) {
        viewModelScope.launch {
            try {
                Log.d("SpO2ViewModel", "🔗 Connessione al dispositivo...")
                _connectedDevice.value = device
                bleConnector.connectToDevice(device)
            } catch (e: Exception) {
                Log.e("SpO2ViewModel", "Errore durante la connessione", e)
                _errorMessage.value = "Errore di connessione: ${e.message}"

                _connectedDevice.value = null

            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            try {
                bleConnector.disconnect()
                _isMeasuring.value = false

                _connectedDevice.value = null

            } catch (e: Exception) {
                Log.e("SpO2ViewModel", "Errore durante la disconnessione", e)
                _errorMessage.value = "Errore di disconnessione: ${e.message}"
            }
        }
    }

    fun saveMeasurement() {
        viewModelScope.launch {
            try {
                Log.d("SpO2ViewModel", "💾 Salvataggio misurazione: SpO₂=${_spO2.value}%, HR=${_heartRate.value}bpm")

                // Qui implementerai le chiamate API per salvare la misurazione
                // Esempio:
                // apiService.saveMeasurement(
                //     spO2 = _spO2.value,
                //     heartRate = _heartRate.value,
                //     timestamp = System.currentTimeMillis()
                // )

                // Per ora, reset dello stato dopo il salvataggio
                _measurementCompleted.value = false
                _spO2.value = 0
                _heartRate.value = 0

                Log.d("SpO2ViewModel", "✅ Misurazione salvata con successo")

            } catch (e: Exception) {
                Log.e("SpO2ViewModel", "Errore durante il salvataggio della misurazione", e)
                _errorMessage.value = "Errore salvataggio: ${e.message}"
            }
        }
    }

    fun startMeasurement() {
        if (_isConnected.value && !_isMeasuring.value) {
            viewModelScope.launch {
                try {
                    bleConnector.startSpO2Measurement()
                    _isMeasuring.value = true
                } catch (e: Exception) {
                    Log.e("SpO2ViewModel", "Errore durante l'avvio della misurazione", e)
                    _errorMessage.value = "Errore misurazione: ${e.message}"
                }
            }
        }
    }

    fun stopMeasurement() {
        viewModelScope.launch {
            try {
                bleConnector.stopSpO2Measurement()
                _isMeasuring.value = false
            } catch (e: Exception) {
                Log.e("SpO2ViewModel", "Errore durante l'arresto della misurazione", e)
                _errorMessage.value = "Errore arresto misurazione: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }


    override fun onCleared() {
        super.onCleared()
        try {
            bleConnector.cleanup()
        } catch (e: Exception) {
            Log.e("SpO2ViewModel", "Errore durante la pulizia", e)
        }    }
}


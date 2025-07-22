package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateMedicalDetectionDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Device
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.MedicalDetectionRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.example.tirociniokelyon.utils.BleConnector
import com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import com.linktop.infs.OnSpO2ResultListener
import com.linktop.whealthService.OnBLEService
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject



@HiltViewModel
class SpO2ViewModel @Inject constructor(
    application: Application,
    private val repository: MedicalDetectionRepository
) : AndroidViewModel(application) {

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

    fun saveMeasurement(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("SpO2ViewModel", "💾 Salvataggio misurazione: SpO₂=${_spO2.value}%, HR=${_heartRate.value}bpm")

                val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                utcFormat.timeZone = TimeZone.getTimeZone("UTC")

                val currentTimestamp = utcFormat.format(Date())


                val medicalDetectionSpO2 = CreateMedicalDetectionDTO(
                    value = _spO2.value.toString(),
                    type = "SPO2",
                    date =  currentTimestamp,
                )
                val medicalDetectionHR = CreateMedicalDetectionDTO(
                    value = _heartRate.value.toString(),
                    type = "HR",
                    date =  currentTimestamp,
                )

                val resultSpO2 = repository.postMedicalDetection(medicalDetectionSpO2)

                resultSpO2.fold(
                    onSuccess = {
                        Log.d("SpO2ViewModel", "✅ SpO2 salvato con successo")
                        saveHeartRate(medicalDetectionHR, onSuccess, onFailure)
                    },
                    onFailure = { error ->
                        Log.e("SpO2ViewModel", "❌ Errore salvataggio SpO2: $error")
                        onFailure()
                    }
                )

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

    private suspend fun saveHeartRate(
        medicalDetectionHR: CreateMedicalDetectionDTO,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val resultHR = repository.postMedicalDetection(medicalDetectionHR)

        resultHR.fold(
            onSuccess = {
                Log.d("SpO2ViewModel", "✅ HR salvato con successo")
                // Reset values only after both are saved successfully
                _measurementCompleted.value = false
                _spO2.value = 0
                _heartRate.value = 0
                onSuccess()
            },
            onFailure = { error ->
                Log.e("SpO2ViewModel", "❌ Errore salvataggio HR: $error")
                onFailure()
            }
        )
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


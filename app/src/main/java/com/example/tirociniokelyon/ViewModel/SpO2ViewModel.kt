package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Device
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import com.example.tirociniokelyon.utils.BleConnector
import com.linktop.infs.OnSpO2ResultListener
import com.linktop.whealthService.OnBLEService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpO2ViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManagerSingleton.getInstance()


    private val bleConnector = BleConnector(application)

    private val _spO2Value = MutableStateFlow(0)
    val spO2Value: StateFlow<Int> = _spO2Value.asStateFlow()

    private val _heartRate = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = _heartRate.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isMeasuring = MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean> = _isMeasuring.asStateFlow()

    private val _fingerDetected = MutableStateFlow(false)
    val fingerDetected: StateFlow<Boolean> = _fingerDetected.asStateFlow()

    private val _isServiceReady = MutableStateFlow(false)
    val isServiceReady: StateFlow<Boolean> = _isServiceReady.asStateFlow()

    private val _deviceList = MutableStateFlow<List<OnBLEService.DeviceSort>>(emptyList())
    val deviceList: StateFlow<List<OnBLEService.DeviceSort>> = _deviceList.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _lastDataReceived = MutableStateFlow(0L)
    val lastDataReceived: StateFlow<Long> = _lastDataReceived.asStateFlow()

    val isBluetoothReady: StateFlow<Boolean> = bluetoothManager.isInitialized
    val bluetoothStatus: StateFlow<String> = bluetoothManager.initializationStatus

    val _deviceConnected = MutableStateFlow<OnBLEService.DeviceSort?>(null)
    val deviceConnected: StateFlow<OnBLEService.DeviceSort?> = _deviceConnected.asStateFlow()

    private var measurementStartTime = 0L
    private var lastSpO2ReceivedTime = 0L
    private var dataReceivedCount = 0


    init {
        Log.d("SpO2ViewModel", "🚀 Inizializzazione SpO2ViewModel")
        observeBluetoothManager()

    }

    private fun observeBluetoothManager() {
        viewModelScope.launch {
            bluetoothManager.isInitialized.collect { isReady ->
                if (isReady) {
                    Log.d("SpO2ViewModel", "✅ BluetoothManager pronto, configurazione listeners")
                    setupBleListeners()
                } else {
                    Log.d("SpO2ViewModel", "⏳ BluetoothManager non ancora pronto")
                }
            }
        }
    }

    private fun setupBleListeners() {
        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        Log.d("SpO2ViewModel", "🔧 Configurazione listener BLE")

        // Listener per stato connessione
        bleConnector.onConnectionStateChanged = { connected ->
            viewModelScope.launch {
                _isConnected.value = connected
                Log.d("SpO2ViewModel", "🔗 Stato connessione: $connected")

                if (connected) {
                    setupSpO2Listeners()

                }
            }
        }

        // Listener per lista dispositivi
        bleConnector.onDeviceListUpdated = { devices ->
            viewModelScope.launch {
                _deviceList.value = devices
                Log.d("SpO2ViewModel", "📱 Lista dispositivi aggiornata: ${devices.size} dispositivi")
            }
        }

        // Listener per dati SpO2
        bleConnector.onSpO2DataReceived = { spo2, hr ->
            viewModelScope.launch {

                val currentTime = System.currentTimeMillis()
                dataReceivedCount++
                lastSpO2ReceivedTime = currentTime

                Log.d("SpO2ViewModel_DEBUG", "📊 Dati SpO2 ricevuti (callback semplice):")
                Log.d("SpO2ViewModel_DEBUG", "  - SpO₂: $spo2%, HR: $hr bpm")
                Log.d("SpO2ViewModel_DEBUG", "  - Contatore dati: $dataReceivedCount")

                _spO2Value.value = spo2
                _heartRate.value = hr
                _lastDataReceived.value = currentTime

                Log.d("SpO2ViewModel", "📊 Dati ricevuti - SpO₂: $spo2%, HR: $hr bpm")
            }
        }

        // Configura listener SpO2 dettagliato
        bleConnector.setSpO2Listener(object : OnSpO2ResultListener {
            override fun onSpO2Result(spo2: Int, pr: Int) {
                viewModelScope.launch {
                    _spO2Value.value = spo2
                    _heartRate.value = pr
                    Log.d("SpO2ViewModel", "📊 SpO₂ Result: $spo2%, PR: $pr bpm")
                }
            }

            override fun onSpO2Wave(wave: Int) {
                Log.d("SpO2ViewModel", "🌊 SpO₂ Wave: $wave")
            }

            override fun onSpO2End() {
                viewModelScope.launch {
                    _isMeasuring.value = false
                    Log.d("SpO2ViewModel", "⏹️ Misurazione SpO₂ terminata")
                }
            }

            override fun onFingerDetection(detected: Int) {
                viewModelScope.launch {
                    _fingerDetected.value = detected == 1
                    Log.d("SpO2ViewModel", "👆 Dito rilevato: ${detected == 1} (valore: $detected)")
                }
            }


        })

        startConnectionStatusMonitoring()



    }

    private fun setupSpO2Listeners() {
        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile per configurare listener SpO2")
            return
        }

        Log.d("SpO2ViewModel", "🫁 Configurazione listener SpO2...")

        // Aspetta un po' prima di configurare i listener per essere sicuri che la connessione sia stabile
        viewModelScope.launch {
            delay(1000)

            // Configura SOLO il listener dettagliato SpO2
            bleConnector.setSpO2Listener(object : OnSpO2ResultListener {
                override fun onSpO2Result(spo2: Int, pr: Int) {
                    viewModelScope.launch {
                        _spO2Value.value = spo2
                        _heartRate.value = pr
                        Log.d("SpO2ViewModel", "📊 SpO₂ Result: $spo2%, PR: $pr bpm")
                    }
                }

                override fun onSpO2Wave(wave: Int) {
                    Log.d("SpO2ViewModel", "🌊 SpO₂ Wave: $wave")
                }

                override fun onSpO2End() {
                    viewModelScope.launch {
                        _isMeasuring.value = false
                        Log.d("SpO2ViewModel", "⏹️ Misurazione SpO₂ terminata")
                    }
                }

                override fun onFingerDetection(detected: Int) {
                    viewModelScope.launch {
                        _fingerDetected.value = detected == 1
                        Log.d("SpO2ViewModel", "👆 Dito rilevato: ${detected == 1} (valore: $detected)")
                    }
                }
            })

            Log.d("SpO2ViewModel", "✅ Listener SpO2 configurati correttamente")
        }
    }

    private fun startConnectionStatusMonitoring() {
        viewModelScope.launch {
            while (true) {
                delay(2000) // Controlla ogni 2 secondi
                val bleConnector = bluetoothManager.getBleConnector()
                val actualConnectionState = bleConnector?.isConnected() ?: false

                if (_isConnected.value != actualConnectionState) {
                    Log.d("SpO2ViewModel", "🔄 Discrepanza stato connessione! UI: ${_isConnected.value}, Reale: $actualConnectionState")
                    _isConnected.value = actualConnectionState
                }
            }
        }
    }




    fun startConnectionTest() {
        Log.d("SpO2ViewModel", "🧪 === INIZIO TEST CONNESSIONE DA VIEWMODEL ===")

        if (!bluetoothManager.isReady()) {
            Log.e("SpO2ViewModel", "❌ BluetoothManager non pronto")
            return
        }

        bluetoothManager.startConnectionTest()
    }

    /**
     * Avvia scansione dispositivi
     */
    fun startScan() {
        Log.d("SpO2ViewModel", "🔍 Avvio scansione dispositivi")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        _isScanning.value = true
        bleConnector.startScan()
    }

    /**
     * Ferma scansione dispositivi
     */
    fun stopScan() {
        Log.d("SpO2ViewModel", "⏹️ Stop scansione dispositivi")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        _isScanning.value = false
        bleConnector.stopScan()
    }
    /**
     * Connette al primo dispositivo disponibile
     */
    fun connectToFirstDevice() {
        Log.d("SpO2ViewModel", "🔗 Connessione al primo dispositivo disponibile")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        bleConnector.connectToFirstAvailable()
    }

    /**
     * Connette a un dispositivo specifico
     */
    fun connectToDevice(device: OnBLEService.DeviceSort) {
        Log.d("SpO2ViewModel", "🔗 Connessione a dispositivo specifico")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        _deviceConnected.value = device
        bleConnector.connectToDevice(device)

    }

    /**
     * Disconnette dal dispositivo
     */
    fun disconnectDevice() {
        Log.d("SpO2ViewModel", "🔌 Disconnessione dispositivo")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        _deviceConnected.value = null
        bleConnector.disconnect()
    }

    /**
     * Aggiorna lista dispositivi
     */
    fun refreshDeviceList() {
        Log.d("SpO2ViewModel", "🔄 Aggiornamento lista dispositivi")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        viewModelScope.launch {
            val devices = bleConnector.getAvailableDevices()
            _deviceList.value = devices
            Log.d("SpO2ViewModel", "📱 Lista dispositivi aggiornata: ${devices.size} dispositivi")
        }
    }

    /**
     * Avvia misurazione SpO2
     */
    fun startMeasurement() {
        Log.d("SpO2ViewModel", "🫁 Avvio misurazione SpO₂")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        if (!bleConnector.isConnected()) {
            Log.e("SpO2ViewModel", "❌ Nessun dispositivo connesso")
            return
        }

        viewModelScope.launch {
            _isMeasuring.value = true
            bleConnector.startSpO2Measurement()

        }
    }

    /**
     * Ferma misurazione SpO2
     */
    fun stopMeasurement() {
        Log.d("SpO2ViewModel", "⏹️ Stop misurazione SpO₂")

        val bleConnector = bluetoothManager.getBleConnector()
        if (bleConnector == null) {
            Log.e("SpO2ViewModel", "❌ BleConnector non disponibile")
            return
        }

        viewModelScope.launch {
            _isMeasuring.value = false
            bleConnector.stopSpO2Measurement()
        }
    }

    /**
     * Ottiene livello batteria
     */
    fun getBatteryLevel(): Int {
        val bleConnector = bluetoothManager.getBleConnector()
        return bleConnector?.getBatteryLevel() ?: -1
    }

    /**
     * Verifica se è connesso a un dispositivo
     */
    fun isDeviceConnected(): Boolean {
        val bleConnector = bluetoothManager.getBleConnector()
        return bleConnector?.isConnected() ?: false
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("SpO2ViewModel", "🧹 ViewModel clearing")
        // Non pulire il BluetoothManager qui, potrebbe essere usato altrove
    }
}
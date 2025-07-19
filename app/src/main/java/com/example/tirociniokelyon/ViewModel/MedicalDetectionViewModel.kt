package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.MedicalDetectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LastDetectionState (
    val lastSPO2: MedicalDetection? = null,
    val lastHR: MedicalDetection? = null,
    val lastTemperature: MedicalDetection? = null,
    val lastWeight: MedicalDetection? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MedicalDetectionsState (
    val medicalDetections : List<MedicalDetection>? = null,
    val currentType: String ? = "WEEK",
    val currentView: String? = "MONTH",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MedicalDetectionViewModel @Inject
constructor( private val repository: MedicalDetectionRepository,

    ): ViewModel(){

    private val _lastDetectionState = MutableStateFlow(LastDetectionState())
    val lastDetectionState: StateFlow<LastDetectionState> = _lastDetectionState

    private val _medicalDetectionsState = MutableStateFlow(MedicalDetectionsState())
    val medicalDetectionsState: StateFlow<MedicalDetectionsState> = _medicalDetectionsState

    // 🔹 Carica ultima rilevazione per ogni tipo
    fun loadLastDetections() {
        viewModelScope.launch {
            _lastDetectionState.update { it.copy(isLoading = true, error = null) }

            try {
                val spo2 = repository.getLastMedicalDetection("SPO2").getOrNull()
                val hr = repository.getLastMedicalDetection("HR").getOrNull()
                val temperature = repository.getLastMedicalDetection("TEMPERATURE").getOrNull()
                val weight = repository.getLastMedicalDetection("WEIGHT").getOrNull()

                _lastDetectionState.update {
                    it.copy(
                        lastSPO2 = spo2,
                        lastHR = hr,
                        lastTemperature = temperature,
                        lastWeight = weight,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _lastDetectionState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // 🔹 Carica tutte le rilevazioni con filtri opzionali
    fun loadMedicalDetections(
        type: String = "ALL",
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _medicalDetectionsState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                val result = repository.getMedicalDetections(type, startDate, endDate)

                result.onSuccess { detections ->
                    _medicalDetectionsState.update {
                        it.copy(
                            medicalDetections = detections,
                            currentType = type,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { e ->
                    _medicalDetectionsState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }

            } catch (e: Exception) {
                _medicalDetectionsState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    // 🔹 Metodo per aggiornare il tipo di visualizzazione (es. chart settimanale/mensile)
    fun updateCurrentView(view: String) {
        _medicalDetectionsState.update {
            it.copy(currentView = view)
        }
    }
}
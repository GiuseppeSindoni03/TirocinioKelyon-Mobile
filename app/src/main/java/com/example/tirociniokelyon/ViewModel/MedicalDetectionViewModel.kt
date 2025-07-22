package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.MedicalDetectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateMedicalDetectionDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale
import java.util.TimeZone


enum class DetectionType(val displayName: String, val unit: String, val placeholder: String, val realName: String) {
    SPO2("SPO2", "%", "Inserisci saturazione (es. 98)", "SPO2"),
    HR("Frequenza Cardiaca", "bpm", "Inserisci battiti (es. 75)", "HR"),
    PESO("Peso", "kg", "Inserisci peso (es. 70.5)", "WEIGHT"),
    TEMPERATURA("Temperatura", "°C", "Inserisci temperatura (es. 36.5)", "TEMPERATURE");


}

data class LastDetectionState(
    val lastSPO2: MedicalDetection? = null,
    val lastHR: MedicalDetection? = null,
    val lastTemperature: MedicalDetection? = null,
    val lastWeight: MedicalDetection? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MedicalDetectionsState(
    val medicalDetections: List<MedicalDetection>? = null,
    val currentType: String? = "ALL",
    val currentView: String? = "WEEK",
    val isLoading: Boolean = false,
    val error: String? = null
)


data class InsertMedicalDetectionState(
    val value: String = "",
    val selectedType: DetectionType = DetectionType.SPO2,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isValid: Boolean = false
)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MedicalDetectionViewModel @Inject
constructor(
    private val repository: MedicalDetectionRepository,

    ) : ViewModel() {


    private val _lastDetectionState = MutableStateFlow(LastDetectionState())
    val lastDetectionState: StateFlow<LastDetectionState> = _lastDetectionState

    private val _insertDetectionState = MutableStateFlow(InsertMedicalDetectionState())
    val insertDetectionState: StateFlow<InsertMedicalDetectionState> = _insertDetectionState


    private val _medicalDetectionsState = MutableStateFlow(MedicalDetectionsState())
    val medicalDetectionsState: StateFlow<MedicalDetectionsState> = _medicalDetectionsState

    init {
        loadLastDetections()
        loadMedicalDetectionsWithCurrentView()
    }
    fun updateValue(value: String) {
        // Permette solo numeri e un punto decimale
        val filteredValue = value.filter { it.isDigit() || it == '.' }
        val isValid = isValidValue(filteredValue, _insertDetectionState.value.selectedType)

        _insertDetectionState.update {
            it.copy(
                value = filteredValue,
                isValid = isValid,
                error = if (isValid) null else getValidationError(_insertDetectionState.value.selectedType)
            )
        }
    }

    fun updateSelectedType(selectedType: DetectionType) {
        val currentValue = _insertDetectionState.value.value
        val isValid = isValidValue(currentValue, selectedType)

        _insertDetectionState.update {
            it.copy(
                selectedType = selectedType,
                isValid = isValid,
                error = if (isValid) null else getValidationError(selectedType)
            )
        }
    }

    private fun isValidValue(value: String, type: DetectionType): Boolean {
        if (value.isEmpty()) return false

        val numericValue = value.toFloatOrNull() ?: return false

        return when (type) {
            DetectionType.SPO2 -> numericValue in 50f..100f
            DetectionType.HR -> numericValue in 30f..220f
            DetectionType.PESO -> numericValue in 20f..300f
            DetectionType.TEMPERATURA -> numericValue in 30f..45f
        }
    }

    private fun getValidationError(type: DetectionType): String {
        return when (type) {
            DetectionType.SPO2 -> "La saturazione deve essere tra 50% e 100%"
            DetectionType.HR -> "La frequenza cardiaca deve essere tra 30 e 220 bpm"
            DetectionType.PESO -> "Il peso deve essere tra 20 e 300 kg"
            DetectionType.TEMPERATURA -> "La temperatura deve essere tra 30°C e 45°C"
        }
    }

    fun resetInsertMedicalDetection() {
        _insertDetectionState.update {
            InsertMedicalDetectionState()
        }
    }

    private fun loadLastDetections() {

        viewModelScope.launch {

            try {
                Log.d("VIEWMODEL", "Setting loading state")

                _lastDetectionState.takeIf { it != null }?.update {
                    it.copy(isLoading = true, error = null)
                } ?: run {
                    Log.e("VIEWMODEL", "_lastDetectionState è null al primo update!")
                    return@launch
                }

                Log.d("VIEWMODEL", "Calling repository methods")

                val spo2 = repository.getLastMedicalDetection("SPO2").getOrNull()
                val hr = repository.getLastMedicalDetection("HR").getOrNull()
                val temperature = repository.getLastMedicalDetection("TEMPERATURE").getOrNull()
                val weight = repository.getLastMedicalDetection("WEIGHT").getOrNull()

                Log.d("VIEWMODEL", "Repository results - SPO2: $spo2, HR: $hr")


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
                Log.e("VIEWMODEL", "Errore in loadLastDetections: ${e.message}", e)
                _lastDetectionState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // 🔹 Carica tutte le rilevazioni con filtri opzionali
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMedicalDetectionsWithCurrentView() {
        val currentState = _medicalDetectionsState.value
        val currentType = currentState.currentType ?: "ALL"
        val currentView = currentState.currentView ?: "WEEK"

        val (startDate, endDate) = getDateRangeForCurrentView(currentView)

        Log.d("VIEWMODEL", "Loading detections - Type: $currentType, View: $currentView")
        Log.d("VIEWMODEL", "Date range: $startDate to $endDate")

        loadMedicalDetections(currentType, startDate, endDate)
    }

    private fun loadMedicalDetections(
        type: String ,
        startDate: String,
        endDate: String
    ) {

        Log.d("VIEWMODEL", "Sto chiamando la repo $type $startDate,--- $endDate")
        viewModelScope.launch {
            _medicalDetectionsState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                val result = repository.getMedicalDetections(type, startDate, endDate)

                result.onSuccess { response ->
                    _medicalDetectionsState.update {
                        it.copy(
                            medicalDetections = response.detections,
                            currentType = type,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { e ->

//                    if(e.cause == 40)
                    _medicalDetectionsState.update {
                        it.copy(
                            isLoading = false,
                            error = e.printStackTrace().toString()
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateCurrentView(view: String) {
        _medicalDetectionsState.update {
            it.copy(currentView = view)
        }

        loadMedicalDetectionsWithCurrentView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateCurrentType (type: String)  {
        _medicalDetectionsState.update {
            it.copy(currentType = type)
        }
        loadMedicalDetectionsWithCurrentView()
    }
    fun postMedicalDetection (

        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val currentState = _insertDetectionState.value

        val type = currentState.selectedType
        val value = currentState.value

        if (!currentState.isValid || currentState.value.isEmpty()) {
            onFailure()
            return
        }


        Log.d("POST_MEDICAL", "Inizio chiamata ViewModel")


        viewModelScope.launch {
            try {

                _insertDetectionState.update { it.copy(isLoading = true, error = null) }


                val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                utcFormat.timeZone = TimeZone.getTimeZone("UTC")

                val medicalDetection = CreateMedicalDetectionDTO(
                    value = value,
                    type = type.realName,
                    date =  utcFormat.format(Date()),
                )
                Log.d("POST_MEDICAL", "Invio di $medicalDetection")

                val result = repository.postMedicalDetection(medicalDetection)

                result.fold(
                    onSuccess = {
                        Log.d("POST_MEDICAL", "Tutt appost fratm")
                        onSuccess()
                    },
                    onFailure = {
                        Log.d("POST_MEDICAL", "Stamm nguagliat")
                        onFailure()
                    }
                )
            } catch (e: Exception) {
                Log.d("POST_MEDICAL", "Errore ${e.message}")
            }
        }

    }
}
@RequiresApi(Build.VERSION_CODES.O)
private fun getDateRangeForCurrentView(view: String): Pair<String, String> {
    val today = LocalDate.now()

    // Usa lo STESSO formato che usi per salvare (UTC con 'Z')
    val utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    return when (view) {
        "WEEK" -> {
            // Lunedì della settimana corrente alle 00:00:00 UTC
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val mondayStart = monday.atStartOfDay(ZoneId.of("UTC"))

            // Domenica della settimana corrente alle 23:59:59.999 UTC
            val sunday = monday.plusDays(6)
            val sundayEnd = sunday.atTime(23, 59, 59, 999_000_000)
                .atZone(ZoneId.of("UTC"))

            Pair(mondayStart.format(utcFormatter), sundayEnd.format(utcFormatter))
        }
        "MONTH" -> {
            // Primo giorno del mese corrente alle 00:00:00 UTC
            val firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
            val monthStart = firstDayOfMonth.atStartOfDay(ZoneId.of("UTC"))

            // Ultimo giorno del mese corrente alle 23:59:59.999 UTC
            val lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
            val monthEnd = lastDayOfMonth.atTime(23, 59, 59, 999_000_000)
                .atZone(ZoneId.of("UTC"))

            Pair(monthStart.format(utcFormatter), monthEnd.format(utcFormatter))
        }
        "3_MONTH" -> {
            // 3 mesi fa dal primo giorno del mese alle 00:00:00 UTC
            val threeMonthsAgo = today.minusMonths(2).with(TemporalAdjusters.firstDayOfMonth())
            val threeMonthStart = threeMonthsAgo.atStartOfDay(ZoneId.of("UTC"))

            // Ultimo giorno del mese corrente alle 23:59:59.999 UTC
            val lastDayOfCurrentMonth = today.with(TemporalAdjusters.lastDayOfMonth())
            val currentMonthEnd = lastDayOfCurrentMonth.atTime(23, 59, 59, 999_000_000)
                .atZone(ZoneId.of("UTC"))

            Pair(threeMonthStart.format(utcFormatter), currentMonthEnd.format(utcFormatter))
        }
        else -> {
            // Default: settimana corrente
            val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val mondayStart = monday.atStartOfDay(ZoneId.of("UTC"))

            val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            val sundayEnd = sunday.atTime(23, 59, 59, 999_000_000)
                .atZone(ZoneId.of("UTC"))

            Pair(mondayStart.format(utcFormatter), sundayEnd.format(utcFormatter))
        }
    }

}


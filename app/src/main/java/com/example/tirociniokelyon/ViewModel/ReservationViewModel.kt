package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.navArgument
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.CreateReservationDTO
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Slot
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.ReservationRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.UserRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


enum class ReservationStatus {
    CONFIRMED, DECLINED, PENDING;

    fun label(): String {
        return when (this) {
            CONFIRMED -> "Confermate"
            DECLINED -> "Rifiutate"
            PENDING -> "In attesa"
        }
    }
}

enum class ReservationVisitType {
    FIRST_VISIT , CONTROL;

    fun label(): String {
        return when (this) {
            FIRST_VISIT -> "FIRST_VISIT"
            CONTROL -> "CONTROL"
        }
    }

    fun nameLabel (): String {
        return when (this) {
            FIRST_VISIT -> "Prima visita"
            CONTROL -> "Visita di controllo"
        }
    }


}



data class UiState(
    val nextReservations: List<Reservation>? = null,
    val doctor: Doctor? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ReservationListState(
    val reservations: List<Reservation>? = null,
    val currentStatus: ReservationStatus = ReservationStatus.CONFIRMED,
    val isLoading: Boolean = false,
    val error: String? = null
)




@HiltViewModel

class ReservationViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _listState = MutableStateFlow(ReservationListState())
    val listState: StateFlow<ReservationListState> = _listState

    private val _slotsState = MutableStateFlow<List<Slot>?>(null)
    val slotsState: StateFlow<List<Slot>?> = _slotsState

    private val _visitType = MutableStateFlow(ReservationVisitType.FIRST_VISIT )
    val visitType: StateFlow<ReservationVisitType> = _visitType

    private val _selectedDay = MutableStateFlow<String?>(null)
    val selectedDay: StateFlow<String?> = _selectedDay

    private val _selectedSlot = MutableStateFlow<Slot?>(null)
    val selectedSlot: StateFlow<Slot?> = _selectedSlot

    private val _isLoadingSlots = MutableStateFlow(false)
    val isLoadingSlots: StateFlow<Boolean> = _isLoadingSlots

    private val _slotsError = MutableStateFlow<String?>(null)
    val slotsError: StateFlow<String?> = _slotsError

    private val _isFirstVisit = MutableStateFlow<Boolean?>(null)
    val isFirstVisit: StateFlow<Boolean?> = _isFirstVisit

    private var _isDialogOpen = MutableStateFlow<Boolean>(false)
    val isDialogOpen : StateFlow<Boolean> = _isDialogOpen

    init {
        loadDoctor()
        loadNextReservation()
        loadReservations(listState.value.currentStatus)

    }

    fun changeReservationStatus(status: ReservationStatus) {
        _listState.update { it.copy(currentStatus = status) }
        loadReservations(status)
    }

    fun changeVisitType(visitType: ReservationVisitType) {
        _visitType.value = visitType
        // Ricarica gli slot se è già stato selezionato un giorno
        if (_selectedDay.value != null) {
            loadSlots()
        }
    }

    fun changeSelectedSlot (slot: Slot) {
        _selectedSlot.value = slot
    }

    fun changeSelectedDay (day: String) {
        _selectedDay.value = day
        _selectedSlot.value = null // Reset slot selection
        loadSlots()

    }

    fun createReservation () {
        val currentSelectedSlot = _selectedSlot.value
        val currentVisitType = _visitType.value


        if (currentSelectedSlot == null) return

        _isLoadingSlots.value = true
        _slotsError.value = null

        Log.d("RESERVATIONS", "Inizio createReservation viewModel")

        viewModelScope.launch {
            try {
                val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                utcFormat.timeZone = TimeZone.getTimeZone("UTC")

                val reservation = CreateReservationDTO(
                    startTime = utcFormat.format(currentSelectedSlot.startTime),
                    endTime = utcFormat.format(currentSelectedSlot.endTime),
                    visitType = currentVisitType.label()
                )


                Log.d("RESERVATIONS", "Invio di $reservation")
                val result = repository.createReservation(reservation)

                result.fold(
                    onSuccess = {
                        _isLoadingSlots.value = false
                        _slotsError.value = null
                        _isDialogOpen.value = true

                        loadReservations(ReservationStatus.PENDING)

                        },
                    onFailure = {
                        exception ->
                        _isLoadingSlots.value = false
                        _slotsError.value = exception.message
                    }
                )


            } catch (e: Exception) {
                Log.d("RESERVATIONS", "Errore createReservation viewModel ${e.message}")
            }
        }
    }

    private fun loadSlots () {
        val currentDay = _selectedDay.value
        val currentVisitType = _visitType.value


        if (currentDay == null) {
            Log.d("RESERVATIONS", "Nessun giorno selezionato")
            return
        }

        Log.d("RESERVATIONS", "Inizio chiamata getSlot ViewModel")


        _isLoadingSlots.value = true
        _slotsError.value = null

        viewModelScope.launch {
            try {
               val result = repository.getSlots(visitType = currentVisitType, date = currentDay)

                Log.d("RESERVATIONS", "Result viewModel: ${result}")
                result.fold(
                    onSuccess = { slots ->
                        _slotsState.value = slots
                        _isLoadingSlots.value = false
                        _slotsError.value = null
                    },
                    onFailure = {
                            exception ->
                        Log.e("DEBUG", "Errore nel caricamento degli slot", exception)
                        _isLoadingSlots.value = false
                        _slotsError.value = exception.message ?: "Errore sconosciuto"
                    }
                )
            } catch (e: Exception) {
                Log.e("DEBUG", "Errore nel caricamento degli slot ${e.message}")
                _isLoadingSlots.value = false
                _slotsError.value = e.message ?: "Errore sconosciuto"

            }
        }
    }

     fun isFirstVisit () {
        Log.d("RESERVATION", "Inizio chiamata isFirstVisit viewModel")

        viewModelScope.launch {
            try {

                val result = repository.isFirstVisit()

                result.fold(
                        onSuccess = { isFirst ->
                            Log.d("RESERVATION", "È first visit? $isFirst")
                            _isFirstVisit.value = isFirst
                            _visitType.value = if (isFirst) {
                                ReservationVisitType.FIRST_VISIT
                            } else {
                                ReservationVisitType.CONTROL
                            }
                        },
                onFailure = { error ->
                    Log.e("RESERVATION", "Errore durante isFirstVisit: ${error.message}")
                    _isFirstVisit.value = null // oppure false o gestisci come preferisci
                }
                )
            } catch (e: Exception) {
                Log.d("RESERVATIONS","Errore ${e.message}")
            }
        }
    }

    private fun loadReservations(status: ReservationStatus) {

        Log.d("RESERVATIONS", "Inizio chiamata view Model")
        _listState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = repository.getReservations(status)

                Log.d("RESERVATIONS", "Result viewModel: ${result}")
                result.fold(
                    onSuccess = { reservations ->
                        _listState.update {
                            it.copy(
                                reservations = reservations,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = {
                        exception ->
                        Log.e("DEBUG", "Errore nel caricamento delle prenotazioni", exception)
                        _listState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Errore sconosciuto"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("RESERVATIONS","Errore nel caricamento delle prenotazioni: ${e.message}")
            }
        }

    }

    private fun loadNextReservation() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = repository.getNextReservations()
                result.fold(
                    onSuccess = { reservations ->
                        _uiState.update {
                            it.copy(
                                nextReservations = reservations,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("DEBUG", "Errore nel caricamento della prossima prenotazione", exception)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Errore sconosciuto"
                            )
                        }
                    }
                )

            } catch (e: Exception) {
                Log.d("DEBUG", "LoadNextReservation ${e.message}")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(error = "${e.message}")
                    }
                }
            }
        }
    }


    private fun loadDoctor() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        if (sessionManager.user.value == null) {
            _uiState.update { it.copy(error = "Utente non autenticato") }
            return
        }
        viewModelScope.launch {
            try {
                val result = userRepository.getDoctor()



                result.fold(
                    onSuccess = { doctor ->
                        _uiState.update {
                            it.copy(
                                doctor = doctor,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("DEBUG", "Errore nel caricamento del dottore", exception)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Errore sconosciuto"
                            )
                        }
                    }
                )

            } catch (e: Exception) {
                Log.d("DEBUG", "${e.message}")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(error = "${e.message}")
                    }
                }
            }
        }
    }

}
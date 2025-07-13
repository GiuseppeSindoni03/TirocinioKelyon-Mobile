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
            FIRST_VISIT -> "Prima visita"
            CONTROL -> "Visita di controllo"
        }
    }
}



data class UiState(
    val nextReservation: Reservation? = null,
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

data class ReservationAddState(
    val slots: List<Slot>? = null,
    val visitType: ReservationVisitType= ReservationVisitType.FIRST_VISIT,
    val selectedDay : String? = null,
    val selectedSlot: Slot? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel

class ReservationViewModel @Inject constructor(
    private val repository: ReservationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _listState = MutableStateFlow(ReservationListState())
    val listState: StateFlow<ReservationListState> = _listState

    private val _addState = MutableStateFlow(ReservationAddState())
    val addState: StateFlow<ReservationAddState> = _addState

    init {
        loadNextReservation()
        loadReservations(listState.value.currentStatus)
        loadDoctor()
    }

    fun changeReservationStatus(status: ReservationStatus) {
        _listState.update { it.copy(currentStatus = status) }
        loadReservations(status)
    }

    fun changeVisitType(visitType: ReservationVisitType) {
        _addState.update { it.copy(visitType = visitType) }
        // Ricarica gli slot se è già stato selezionato un giorno
        if (_addState.value.selectedDay != null) {
            loadSlots()
        }
    }

    fun changeSelectedSlot (slot: Slot) {
        _addState.update { it.copy(selectedSlot = slot) }
    }

    fun changeSelectedDay (day: String) {
        _addState.update { it.copy(selectedDay = day, selectedSlot = null) }

        loadSlots()
    }

    fun createReservation () {
        _addState.update { it.copy(isLoading = true, error = null) }

        Log.d("RESERVATIONS", "Inizio createReservation viewModel")

        viewModelScope.launch {
            try {

                val reservation = CreateReservationDTO(
                    startTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(addState.value.selectedSlot!!.startTime),
                    endTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(addState.value.selectedSlot!!.endTime),
                    visitType = addState.value.visitType.toString(),

                )

                Log.d("RESERVATIONS", "Invio di $reservation")
                val result = repository.createReservation(reservation)

                result.fold(
                    onSuccess = {
                        _addState.update { it.copy( isLoading = false, error =null) }
                    },
                    onFailure = {
                        exception ->
                        _addState.update { it.copy( isLoading = false, error = exception.message) }

                    }
                )


            } catch (e: Exception) {
                Log.d("RESERVATIONS", "Errore createReservation viewModel ${e.message}")
            }
        }
    }

    private fun loadSlots () {
        val currentState = _addState.value

        if (currentState.selectedDay == null) {
            Log.d("RESERVATIONS", "Nessun giorno selezionato")
            return
        }

        Log.d("RESERVATIONS", "Inizio chiamata getSlot ViewModel")

        _addState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
               val result = repository.getSlots(visitType = _addState.value.visitType!!, date = _addState.value.selectedDay!!)

                Log.d("RESERVATIONS", "Result viewModel: ${result}")
                result.fold(
                    onSuccess = { slots ->
                        _addState.update {
                            it.copy(
                                slots = slots,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = {
                            exception ->
                        Log.e("DEBUG", "Errore nel caricamento degli slot", exception)
                        _addState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Errore sconosciuto"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("DEBUG", "Errore nel caricamento degli slot ${e.message}")


            }
        }
    }

//    private fun addReservation(visitType: ReservationVisitType) {
//
//        Log.d("RESERVATIONS", "Inizio chiamata view Model")
//        _listState.update { it.copy(isLoading = true, error = null) }
//        viewModelScope.launch {
//            try {
//                val result = repository.getReservations(status)
//
//                Log.d("RESERVATIONS", "Result viewModel: ${result}")
//                result.fold(
//                    onSuccess = { reservations ->
//                        _listState.update {
//                            it.copy(
//                                reservations = reservations,
//                                isLoading = false,
//                                error = null
//                            )
//                        }
//                    },
//                    onFailure = {
//                            exception ->
//                        Log.e("DEBUG", "Errore nel caricamento delle prenotazioni", exception)
//                        _listState.update {
//                            it.copy(
//                                isLoading = false,
//                                error = exception.message ?: "Errore sconosciuto"
//                            )
//                        }
//                    }
//                )
//            } catch (e: Exception) {
//                Log.e("RESERVATIONS","Errore nel caricamento delle prenotazioni: ${e.message}")
//            }
//        }
//
//    }

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
                val result = repository.getNextReservation()
                result.fold(
                    onSuccess = { reservation ->
                        _uiState.update {
                            it.copy(
                                nextReservation = reservation,
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
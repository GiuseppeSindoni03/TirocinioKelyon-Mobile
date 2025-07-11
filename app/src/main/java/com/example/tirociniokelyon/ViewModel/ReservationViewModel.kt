package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.ReservationRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject


enum class ReservationStatus {
    CONFIRMED, DECLINED, PENDING
}

fun createDate(day: Int, month: Int, year: Int, hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, hour, minute, 0) // month è 0-based
    return calendar.time
}
data class UiState(
    val nextReservation: Reservation? = null,
    val reservations: List<Reservation>? = null,
    val doctor: Doctor? = null,
    val status: ReservationStatus = ReservationStatus.CONFIRMED,
    val isLoading: Boolean = false,
    val error: String? = null

)

@HiltViewModel

class ReservationViewModel @Inject constructor(private val repository: ReservationRepository,
    private val userRepository: UserRepository
    ): ViewModel(){

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState



    init {
      loadNextReservation()
//        loadReservations(uiState.value.status)
      loadDoctor()
    }

    private fun loadReservations(status: ReservationStatus) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = repository.getReservations(status)
            } catch (e: Exception) {

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
                                nextReservation  = reservation,
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
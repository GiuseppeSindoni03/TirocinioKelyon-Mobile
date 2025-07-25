package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.DoctorRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.ReservationRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.UserRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val repository: UserRepository,
//    private val reservationRepository: ReservationRepository,
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = sessionManager.user


    data class UiState(
        val user: User? = null,
        val doctor: Doctor? = null,
        val reservation: Reservation? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState


    init {
        loadDoctor()
//        loadReservation()
    }

    fun getMe(): User? {
        return currentUser.value
    }

//    private fun loadReservation() {
//        _uiState.update { it.copy(isLoading = true, error = null) }
//        viewModelScope.launch {
//            try {
//                val result = reservationRepository.getNextReservations()
//                result.fold(
//                    onSuccess = { reservations ->
//                        _uiState.update {
//                            it.copy(
//                                reservation = reservations?.get(0),
//                                isLoading = false,
//                                error = null
//                            )
//                        }
//                    },
//                    onFailure = { exception ->
//                        Log.e(
//                            "HomeViewModel",
//                            "Errore nel caricamento della next reservaton",
//                            exception
//                        )
//                        _uiState.update {
//                            it.copy(
//                                reservation = null,
//                                isLoading = false,
//                                error = exception.message ?: "Errore sconosciuto"
//                            )
//                        }
//                    }
//                )
//
//            } catch (e: Exception) {
//                Log.d("DEBUG", "${e.message}")
//                kotlinx.coroutines.withContext(Dispatchers.Main) {
//                    _uiState.update {
//
//                        it.copy(
//                            isLoading = false, reservation = null,
//                            error = "${e.message}"
//                        )
//                    }
//                }
//            }
//        }
//    }

    private fun loadDoctor() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        if (sessionManager.user.value == null) {
            _uiState.update { it.copy(error = "Utente non autenticato") }
            return
        }
        viewModelScope.launch {
            try {


                val result = doctorRepository.getDoctor()

                result.fold(
                    onSuccess = {

                            doctor ->
                        _uiState.update {
                            it.copy(
                                doctor = doctor,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Errore nel caricamento del dottore", exception)
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





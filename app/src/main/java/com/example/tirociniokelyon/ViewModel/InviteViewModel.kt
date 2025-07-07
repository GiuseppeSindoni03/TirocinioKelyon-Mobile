package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.AcceptInviteRequest
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Invite
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.AuthRepositoryImpl
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.InviteRepository
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val repository: InviteRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val invite: Invite? = null,
        val acceptInviteRequest: AcceptInviteRequest? = null,
        val error: String? = null,
        val acceptInviteSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState



    fun getInvite(inviteId: String) {
        Log.d("DEBUG", "InviteViewModel: Iniziando getInvite per ID: $inviteId")

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                Log.d("DEBUG", "InviteViewModel: Chiamando repository.getInvite")
                val result = repository.getInvite(inviteId)

                result.onSuccess { invite ->
                    Log.d("DEBUG", "InviteViewModel: Invito ricevuto con successo: $invite")
                    _uiState.update { it.copy(invite = invite, isLoading = false) }
                }.onFailure { error ->
                    Log.e("DEBUG", "InviteViewModel: Errore nel recupero invito: ${error.message}")
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "InviteViewModel: Eccezione in getInvite: ${e.message}")
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }


    fun acceptInvite (inviteId: String, request : AcceptInviteRequest) {
        Log.d("DEBUG", "InviteViewModel: Iniziando acceptInvite per ID: $inviteId")

        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isLoading = true, error = null) }

                    val result = repository.acceptInvite(inviteId = inviteId, userInfo = request)

                    result.onSuccess { response ->
                        Log.d("DEBUG", "InviteViewModel: Invito accettato con successo: $response")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                acceptInviteSuccess = true
                            )
                        }
                    }.onFailure { error ->
                        Log.e("DEBUG", "InviteViewModel: Errore nell'accettazione invito: ${error.message}")
                        _uiState.update { it.copy(error = error.localizedMessage, isLoading = false) }
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG", "InviteViewModel: Eccezione in acceptInvite: ${e.message}")
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetAcceptInviteSuccess() {
        _uiState.update { it.copy(acceptInviteSuccess = false) }
    }


}
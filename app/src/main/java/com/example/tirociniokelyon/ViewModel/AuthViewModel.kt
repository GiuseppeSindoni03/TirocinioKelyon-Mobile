package com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.SignInCredentialsDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.Repository.AuthRepositoryImpl
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    data class UiState(
        val email: String = "g@gmail.com",
        val password: String = "Giuseppe1!",
        val passwordIsVisible: Boolean= false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    val currentUser : StateFlow<User?> = sessionManager.user


    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }
    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("DEBUG", "Inizio chiamata")
                val result = repository.signIn(SignInCredentialsDTO(_uiState.value.email, _uiState.value.password))


                sessionManager.setUser(user = result.getOrThrow())
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("DEBUG", "${e.message}")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    _uiState.update {it.copy(error = "${e.message}")
                }
               }
            }
        }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(error = message) }
    }

//    fun getMe (onSuccess: (User) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val result = repository.getMe()
//                kotlinx.coroutines.withContext(Dispatchers.Main) {
//                    onSuccess(result.getOrThrow())
//                }
//            } catch (e: Exception) {
//                Log.d("DEBUG", "${e.message}")
//                kotlinx.coroutines.withContext(Dispatchers.Main) {
//                    _uiState.update {it.copy(error = "${e.message}")
//                    }
//            }
//        }
//    }
//}


    fun logout (onSuccess: () -> Unit) {
        viewModelScope.launch (Dispatchers.IO ){
            try {
                Log.d("DEBUG", "Inizio chiamata logout")
                val result = repository.logout()

                Log.d("DEBUG", "Result logout: ${result}")

                sessionManager.clearUser()
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("DEBUG", "${e.message}")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    _uiState.update {it.copy(error = "Errore durante il logout: ${e.message} ")
                    }
                }
            }
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {it.copy(passwordIsVisible = !!uiState.value.passwordIsVisible)

    }
    }

}


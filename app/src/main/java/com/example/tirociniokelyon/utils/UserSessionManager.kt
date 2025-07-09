package com.example.tirociniokelyon.com.example.tirociniokelyon.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIAuth
import com.example.tirociniokelyon.com.example.tirociniokelyon.remote.APIuser
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cookieJar: PersistentCookieJar,
    private val apiUser: APIuser
) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    companion object {
        private const val USER_KEY = "current_user"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    init {
        loadUserFromPreferences()
    }

    fun setUser(user: User) {
        _user.value = user
        saveUserToPreferences(user)
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN_KEY, true).apply()
    }

    fun clearUser() {
        _user.value = null
        sharedPreferences.edit().clear().apply()
        cookieJar.clearCookies()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false) && _user.value != null
    }

    suspend fun validateSession(): Boolean {
        if (!isLoggedIn()) return false

        return try {
            val response = apiUser.getMe()
            if (response.isSuccessful) {
                // Aggiorna i dati utente se necessario
                response.body()?.let { user ->
                    setUser(user)
                }
                true
            } else {
                // Se la risposta non è successful, pulisci la sessione
                clearUser()
                false
            }
        } catch (e: Exception) {
            // In caso di errore di rete o altro, pulisci la sessione
            clearUser()
            false
        }
    }
    private fun saveUserToPreferences(user: User) {
        val json = gson.toJson(user)
        sharedPreferences.edit().putString(USER_KEY, json).apply()
    }

    private fun loadUserFromPreferences() {
        val json = sharedPreferences.getString(USER_KEY, null)
        if (json != null && sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)) {
            try {
                val user = gson.fromJson(json, User::class.java)
                _user.value = user
            } catch (e: Exception) {
                // Se c'è un errore nel parsing, pulisci le preferenze
                clearUser()
            }
        }
    }
}
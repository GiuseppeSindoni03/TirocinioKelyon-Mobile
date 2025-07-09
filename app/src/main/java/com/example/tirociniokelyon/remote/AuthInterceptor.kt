package com.example.tirociniokelyon.com.example.tirociniokelyon.utils

import android.content.Context
import android.content.Intent
import com.example.tirociniokelyon.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userSessionManagerProvider: Provider<UserSessionManager>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Se ricevi un 401, pulisci la sessione e reindirizza al login
        if (response.code == 401) {
            handleUnauthorized()
        }

        return response
    }

    private fun handleUnauthorized() {
        // Pulisci la sessione utente
        userSessionManagerProvider.get().clearUser()

        // Reindirizza alla MainActivity (che farà il controllo e mostrerà il login)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("force_login", true)
        }
        context.startActivity(intent)
    }
}
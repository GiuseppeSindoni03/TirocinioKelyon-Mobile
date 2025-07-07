package com.example.tirociniokelyon.com.example.tirociniokelyon.utils

import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserSessionManager @Inject constructor(){

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun setUser( user: User) {
        _user.value = user
    }

    fun clearUser () {
        _user.value = null
    }
}
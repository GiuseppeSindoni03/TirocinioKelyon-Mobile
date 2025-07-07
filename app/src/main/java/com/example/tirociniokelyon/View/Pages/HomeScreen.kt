package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.AuthViewModel
import androidx.compose.foundation.layout.padding

import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tirociniokelyon.View.Components.AppTopBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetEdgeToEdgeSystemBars
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetSystemBarStyle

@Composable
fun HomeScreen(navController: NavController,

               )  {

    SetSystemBarStyle(statusBarColor = Color.Transparent, darkIcons = true)
    SetEdgeToEdgeSystemBars(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        darkIcons = true
    )

    val viewModel: AuthViewModel = hiltViewModel()

    val user by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMe {
            user -> Log.d("USER","User:  ${user.name} ${user.surname} ${user.cf}")
        }
    }


    Column {
        Text("Benvenuto nella home page ${user?.name}")

    }

    Scaffold (
        topBar = {
//            AppTopBar()
        },
        bottomBar = {
            NavBar(navController = navController) }
    ) {
        paddingValues ->
        Box  (
            modifier =  Modifier
                .padding(paddingValues)
        ){
            Column {
                Text(text = "Home page")

            }
        }
    }



}
package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NextReservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ReservationsList
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationStatus
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationScreen (navController: NavController) {
    
    val viewModel: ReservationViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val listState by viewModel.listState.collectAsState()


    Scaffold (
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {
                Text(
                    text = "Prenotazioni",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
            }

        },

        bottomBar = {
            NavBar(navController = navController)
        }
    ) {
        paddingValues ->  Column (
            modifier = Modifier.padding(paddingValues)
        ){

            when {
                uiState.isLoading -> {
                    LoadingComponent()
                }

                uiState.error != null -> {
                    ErrorComponent(error = uiState.error.toString())
                }






                else -> {
                    if( uiState.nextReservation != null && uiState.doctor != null) {
                        NextReservation(reservation = uiState.nextReservation!!, doctor = uiState.doctor!!)

                    }


                    if ( listState.reservations != null && uiState.doctor != null)  {
                        ReservationsList(reservations = listState.reservations!!, doctor = uiState.doctor!!, onStatusChange =   { status: ReservationStatus -> viewModel.changeReservationStatus(status) }, currentStatus = listState.currentStatus )
                    }
                }
            }

    }
    }
}



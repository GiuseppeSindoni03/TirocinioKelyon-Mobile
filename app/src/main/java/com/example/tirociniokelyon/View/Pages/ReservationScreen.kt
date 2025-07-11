package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MiniReservationCard
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NextReservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ReservationCard
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ReservationsList
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.createDate
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationScreen (navController: NavController) {
    
    val viewModel: ReservationViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val reservationMocked =  mutableListOf<Reservation>(
        Reservation(
            id = "1",
            status = "CONFIRMED",
            createAt = createDate(1, 7, 2025, 10, 30),
            startTime = createDate(15, 7, 2025, 9, 0),
            endTime = createDate(15, 7, 2025, 9, 30),
            visitType = "FIRST_VISIT"
        ),
        Reservation(
            id = "2",
            status = "PENDING",
            createAt = createDate(3, 7, 2025, 14, 15),
            startTime = createDate(18, 7, 2025, 14, 30),
            endTime = createDate(18, 7, 2025, 15, 0),
            visitType = "CONTROL_VISIT"
        ),
        Reservation(
            id = "3",
            status = "CONFIRMED",
            createAt = createDate(5, 7, 2025, 16, 45),
            startTime = createDate(22, 7, 2025, 11, 15),
            endTime = createDate(22, 7, 2025, 11, 45),
            visitType = "FIRST_VISIT"
        ),
        Reservation(
            id = "4",
            status = "COMPLETED",
            createAt = createDate(28, 6, 2025, 9, 20),
            startTime = createDate(8, 7, 2025, 16, 0),
            endTime = createDate(8, 7, 2025, 16, 30),
            visitType = "CONTROL_VISIT"
        ),
        Reservation(
            id = "5",
            status = "CONFIRMED",
            createAt = createDate(6, 7, 2025, 11, 30),
            startTime = createDate(25, 7, 2025, 10, 45),
            endTime = createDate(25, 7, 2025, 11, 15),
            visitType = "FIRST_VISIT"
        ),
        Reservation(
            id = "6",
            status = "CANCELLED",
            createAt = createDate(2, 7, 2025, 13, 15),
            startTime = createDate(20, 7, 2025, 15, 30),
            endTime = createDate(20, 7, 2025, 16, 0),
            visitType = "CONTROL_VISIT"
        )
    )

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

                    //                uiState.reservations != null &&
                    if (   uiState.doctor != null)  {
                        ReservationsList(reservations = reservationMocked, doctor = uiState.doctor!!)
                    }
                }
            }

    }
    }
}



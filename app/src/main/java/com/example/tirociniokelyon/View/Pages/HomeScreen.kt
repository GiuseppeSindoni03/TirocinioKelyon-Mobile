package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.DoctorInfoCard

import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetEdgeToEdgeSystemBars
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetSystemBarStyle
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.HomeViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.UserDoctor
import java.time.ZonedDateTime
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,

    ) {

    SetSystemBarStyle(statusBarColor = Color.Transparent, darkIcons = true)
    SetEdgeToEdgeSystemBars(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        darkIcons = true
    )

    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.currentUser.collectAsState()


    LaunchedEffect(Unit) {
        Log.d("DEBUG", "Sondo nella home page")
    }


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {
                val now = LocalDateTime.now()
                val hour = now.hour

                val saluto = when (hour) {
                    in 5..11 -> "Buongiorno,"
                    in 12..17 -> "Buon pomeriggio,"
                    else -> "Buonasera,"
                }

                Text(
                    text = "$saluto ${user?.name}",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
                Text(
                    text = "Come ti senti oggi?", modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )


                Log.d("USER", "$user")


            }
        },
        bottomBar = {
            NavBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {

            when {
                uiState.isLoading ->
                    LoadingComponent()

                uiState.error != null ->
                    ErrorComponent(error = uiState.error.toString())


                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        DoctorInfoCard(doctor = uiState.doctor!!)
                        
                        Spacer(modifier = Modifier.height(8.dp))



                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            item {
                                Text(
                                    text = "Attivita' recenti",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }


                            if (uiState.reservation != null) {
                                item {
                                    NextReservationCard(
                                        reservation = uiState.reservation!!,
                                        doctor = uiState.doctor!!
                                    )
                                }
                            }

                        }

                    }


                }


            }
        }


    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextReservationCard(
    reservation: Reservation,
    doctor: Doctor,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Calendario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column (modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Appuntamento confermato",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatDateTime(reservation.startDate.toString()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }


    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun formatDateTime(dateTime: String): String {
    return try {
        // Pattern per il formato in ingresso: "Sat Jul 12 10:30:00 GMT+2:00 2025"
        val inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'xxx yyyy", Locale.ENGLISH)

        // Parsing della data con timezone
        val zonedDateTime = ZonedDateTime.parse(dateTime, inputFormatter)

        // Formatter per il formato desiderato in italiano
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.ITALIAN)

        // Formattazione della data
        zonedDateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        // Gestione errori di parsing
        "Data non valida"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun NextReservationCardPreview() {

    val user = UserDoctor(
        id = "1",
        name = "Mario",
        surname = "Rossi",
        email = "mario.rossi@example.com",
        birthDate = "1990-01-01",
        cf = "RSSMRA90A01H501Z",
        gender = "M",
        phone = "1234567890",
        role = "DOCTOR",
        address = "Via Roma",
        city = "Napoli",
        cap = "80100",
        province = "NA",
    )

    val doctor = Doctor(
        userId = "1",
        orderType = "",
        orderProvince = "",
        orderDate = "",
        registrationNumber = "",
        medicalOffice = "",
        specialization = "",
        user = user
    )

    val reservation = Reservation(
        id = "res1",
        status = "CONFIRMED",
        createAt = Date(),
        startDate = Date(),
        endDate = Date(Date().time + 30 * 60 * 1000), // +30 minuti
        visitType = "Controllo Generico"
    )

    MaterialTheme {
        NextReservationCard(
            reservation = reservation,
            doctor = doctor
        )
    }
}

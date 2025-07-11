package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsList (reservations: List<Reservation>, doctor: Doctor) {


    Column (modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Richieste",
            modifier = Modifier
                .padding(top = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
        )


        LazyColumn(modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(reservations) {
                reservation ->

                MiniReservationCard(reservation = reservation, doctor = doctor)
            }
        }
    }


}
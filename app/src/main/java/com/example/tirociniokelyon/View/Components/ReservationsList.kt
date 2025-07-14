package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationStatus
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation




@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationsList (
    reservations: List<Reservation>, doctor: Doctor, currentStatus: ReservationStatus = ReservationStatus.CONFIRMED,
    onStatusChange: (ReservationStatus) -> Unit
) {
    Log.d("RESERVATIONS", "Sono dentro la reservation List")

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Richieste",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),

        ) {
            val statusOptions = listOf(
                ReservationStatus.CONFIRMED,
                ReservationStatus.PENDING,
                ReservationStatus.DECLINED
            )

//            Log.d("DEBUG", "CurrentStatus = $currentStatus")


            statusOptions.forEachIndexed { index, status ->
                Log.d("DEBUG", "Rendering button: ${status.label()} ($status)")

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = statusOptions.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),

                    onClick = { onStatusChange(status) },
                    selected = currentStatus == status,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,    // sfondo selezionato
                        activeContentColor = Color.White,           // testo selezionato
                        inactiveContainerColor = Color.White,
//                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow, // sfondo NON selezionato
                        inactiveContentColor = Color.Black          // testo NON selezionato
                    )
                ) {
                    Text(text = status.label().toUpperCase(), style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        if (reservations.isNotEmpty()) {
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
        else {
            val text = when (currentStatus) {
                ReservationStatus.DECLINED -> "Nessuna prenotazione rifiutata."
                ReservationStatus.PENDING -> "Nessuna prenotazione in attesa."
                ReservationStatus.CONFIRMED -> "Nessuna prenotazione confermata."
            }
            EmptyAnimation(text)
        }
        
    }


}

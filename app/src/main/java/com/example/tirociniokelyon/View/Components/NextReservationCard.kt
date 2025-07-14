package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextReservations (reservations: List<Reservation>, doctor: Doctor) {




    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = if(reservations.size > 1 ) "Prossimi appuntamenti" else "Prossimi appuntamento",
            modifier = Modifier
                .padding(top = 8.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
        )

        LazyRow ( horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),

        ) {


            items (reservations ){ reservation ->
                ReservationCard(reservation = reservation, doctor = doctor)
            }


        }





    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationCard(
    reservation: Reservation,
    doctor: Doctor,
    modifier: Modifier = Modifier
) {

    val dayFormat = SimpleDateFormat("dd", Locale.ITALIAN)
    val monthFormat = SimpleDateFormat("MMM", Locale.ITALIAN)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.ITALIAN)

    val day = dayFormat.format(reservation.startTime)
    val month = monthFormat.format(reservation.startTime).uppercase()
    val time = timeFormat.format(reservation.startTime)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,

        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),

            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Box
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .width(80.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = day,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = month,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 6.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {

                val visitType =
                    if (reservation.visitType == "FIRST_VISIT") "Prima visita" else "Visita di controllo"
                Text(
                    text = visitType,
                    style = MaterialTheme.typography.displayLarge,
//                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Doctor name
                Text(
                    text = "Dr. ${doctor.user.name} ${doctor.user.surname}",
                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Time and Address
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = "clock")

                    }

                    Text(
                        text = time, style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )


                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "clock")

                    }
                    Text(
                        text = doctor.medicalOffice.toString(),
                        style = MaterialTheme.typography.bodyLarge, color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


            }
        }
    }
}

package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MiniReservationCard(
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
            .height(80.dp)
            .padding(top = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
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
                    .height(80.dp)
                    .width(80.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 6.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 6.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = month,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Content
            Column(
                modifier = Modifier
                    .fillMaxHeight()
//                    .weight(1f)
                    .padding(top = 6.dp, start = 4.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                val visitType = if (reservation.visitType == "FIRST_VISIT") "Prima visita" else "Controllo"
                Text(
                    text = visitType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Doctor name
                Text(
                    text = "Dr. ${doctor.user.name} ${doctor.user.surname}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                            Box(
                                modifier = Modifier
                                    .size(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = "clock")

                            }
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )



                            Box(
                                modifier = Modifier
                                    .size(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "clock")

                            }
                            Text(
                                text = doctor.medicalOffice.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )


                    }
                     }



            }
        }
    }
}
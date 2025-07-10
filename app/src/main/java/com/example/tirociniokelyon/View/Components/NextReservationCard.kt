package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class CardSize {
    Small, Big
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationCard(
    reservation: Reservation,
    doctor: Doctor,
    size: CardSize = CardSize.Big,
    modifier: Modifier = Modifier
) {

//    val dayFormat = SimpleDateFormat("dd", Locale.ITALIAN)
//    val monthFormat = SimpleDateFormat("MMM", Locale.ITALIAN)
//    val timeFormat = SimpleDateFormat("HH:mm", Locale.ITALIAN)

    val cardHeight = when (size) {
        CardSize.Small -> 80.dp
        CardSize.Big -> 100.dp
    }

    val dateBoxSize = when (size) {
        CardSize.Small -> 50.dp
        CardSize.Big -> 60.dp
    }

    val titleFontSize = when (size) {
        CardSize.Small -> 14.sp
        CardSize.Big -> 16.sp
    }

    val doctorFontSize = when (size) {
        CardSize.Small -> 12.sp
        CardSize.Big -> 14.sp
    }

    val detailsFontSize = when (size) {
        CardSize.Small -> 11.sp
        CardSize.Big -> 12.sp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Box
            Box(
                modifier = Modifier
                    .size(dateBoxSize)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4285F4)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "15 giugno - 15:00",
                        color = Color.White,
                        fontSize = if (size == CardSize.Small) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "15 giugno - 15:00",
                        color = Color.White,
                        fontSize = if (size == CardSize.Small) 10.sp else 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title
                Text(
                    text = reservation.visitType,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Doctor name
                Text(
                    text = "Dr. ${doctor.user.name} ${doctor.user.surname}",
                    fontSize = doctorFontSize,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4285F4),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Time and Address
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "-",
                            fontSize = detailsFontSize
                        )
                        Text(
                            text = "15 giugno - 15:00", fontSize = detailsFontSize,
                            color = Color.Gray
                        )
                    }

                    // Address
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "-",
                            fontSize = detailsFontSize
                        )
                        Text(
                            text = doctor.medicalOffice.toString(),
                            fontSize = detailsFontSize,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

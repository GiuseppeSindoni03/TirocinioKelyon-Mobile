package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextReservationCardHome(
    reservation: Reservation,
    modifier: Modifier = Modifier
) {
    GenericCard(
        icon = Icons.Filled.CalendarMonth,
        title = "Appuntamento confermato",
        description = formatDateTime(reservation.startTime.toString()),
        modifier = modifier
    )
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
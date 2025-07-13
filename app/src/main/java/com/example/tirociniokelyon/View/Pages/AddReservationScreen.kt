package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.DaySelector
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.EmptyAnimation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationStatus
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationVisitType
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Slot
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddReservationScreen(navController: NavController) {

    val viewModel: ReservationViewModel = hiltViewModel()

    val uiState by viewModel.addState.collectAsState()


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top

            ) {
                Text(
                    text = "Aggiungi prenotazione",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
            }
        },
        bottomBar = {
            Column {
                Button(
                    onClick = { viewModel.createReservation() },
                    enabled = uiState.selectedDay != null && uiState.selectedSlot != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Conferma",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                NavBar(navController = navController)
            }
        }

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    LoadingComponent()
                }

                uiState.error != null -> {
                    ErrorComponent(error = uiState.error.toString())
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        ) {

                        SelectedVisitType(
                            onVisitTypeChange = { reservationVisitType ->
                                viewModel.changeVisitType(reservationVisitType)
                            },
                            currentVisitType = uiState.visitType
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Seleziona giorno",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        DaySelector(
                            selectedDate = uiState.selectedDay,
                            onDateSelected = { selectedDay ->
                                viewModel.changeSelectedDay(selectedDay)
                            })

                        Spacer(modifier = Modifier.height(40.dp))

                        if (uiState.slots != null) {
                            SlotsList(
                                slots = uiState.slots!!,
                                onSelectedSlot = { slot -> viewModel.changeSelectedSlot(slot) },
                                selectedSlot = uiState.selectedSlot
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))





                    }
                }
            }
        }


    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatSlotTime(startTime: Date): String {
    val instant = startTime.toInstant()
    val localDateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())
    return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SlotsList(slots: List<Slot>, onSelectedSlot: (Slot) -> Unit, selectedSlot: Slot?) {

    Log.d("RESERVATIONS", "Slot: $slots")

    if (slots.isEmpty()) {
        EmptyAnimation("Nessun orario disponibile")

    } else {

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp)

        ) {
            items(slots) { slot ->
                SlotItem(
                    slot,
                    onClick = { onSelectedSlot(slot) },
                    isSelected = selectedSlot == slot
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))


    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SlotItem(
    slot: Slot, onClick: () -> Unit, isSelected: Boolean,
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                Color.White
        ),
        onClick = { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatSlotTime(slot.startTime),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black,
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedVisitType(
    currentVisitType: ReservationVisitType = ReservationVisitType.FIRST_VISIT,
    onVisitTypeChange: (ReservationVisitType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
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
            val visitOptions = listOf(
                ReservationVisitType.FIRST_VISIT,
                ReservationVisitType.CONTROL
            )

            Log.d("DEBUG", "VisitType = $visitOptions")


            visitOptions.forEachIndexed { index, visitType ->
                Log.d("DEBUG", "Rendering button: ${visitType.label()} ($visitType)")

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = visitOptions.size,
                        baseShape = RoundedCornerShape(12.dp)
                    ),

                    onClick = { onVisitTypeChange(visitType) },
                    selected = currentVisitType == visitType,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,    // sfondo selezionato
                        activeContentColor = Color.White,           // testo selezionato
                        inactiveContainerColor = Color.White,
//                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow, // sfondo NON selezionato
                        inactiveContentColor = Color.Black          // testo NON selezionato
                    )
                ) {
                    Text(
                        text = visitType.label().toUpperCase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


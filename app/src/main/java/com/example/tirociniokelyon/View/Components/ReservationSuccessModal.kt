package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.formatSlotTime
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.ReservationVisitType
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Slot

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationSuccessDialog(
    selectedSlot: Slot?,
    selectedDay: String?,
    visitType: ReservationVisitType,
    onDismiss: () -> Unit,
) {
    // Controllo early return per parametri null
    if (selectedSlot == null || selectedDay == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                modifier = Modifier.size(64.dp),
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(32.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        title = {
            Text(
                text = "Richiesta inviata con successo",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Informazione giorno
                        ReservationInfoRow(
                            label = "Giorno",
                            value = selectedDay
                        )

                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )

                        // Informazione orario
                        ReservationInfoRow(
                            label = "Orario",
                            value = "${formatSlotTime(selectedSlot.startTime)} - ${formatSlotTime(selectedSlot.endTime)}"
                        )

                        Divider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp
                        )

                        // Informazione tipo visita
                        ReservationInfoRow(
                            label = "Tipo visita",
                            value = visitType.nameLabel()
                        )

                }

            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = Color.Black,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ReservationInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}
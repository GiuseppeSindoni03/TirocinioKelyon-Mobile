package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

data class DayItem(
    val date: Date,
    val displayDay: String,
    val displayMonth: String,
    val displayWeekday: String,
    val dateString: String,
    val isToday: Boolean = false
)

@Composable
fun DaySelector(
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = remember { generateNext21Days() }

    LaunchedEffect(selectedDate) {
        if (selectedDate == null) {
            onDateSelected(days[0].dateString)
        }
    }

    Log.d("RESERVATIONS", "Sono dentro il Day selector $days")


    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { dayItem ->
            DayCard(
                dayItem = dayItem,
                isSelected = selectedDate == dayItem.dateString,
                onClick = { onDateSelected(dayItem.dateString) }
            )
        }
    }
}

@Composable
private fun DayCard(
    dayItem: DayItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(70.dp)
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Giorno della settimana

            Text(
                text = dayItem.displayDay,
                style = MaterialTheme.typography.headlineSmall,
                color = if (isSelected) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dayItem.displayWeekday,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White else Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            // Numero del giorno


            // Mese
            Text(
                text = dayItem.displayMonth,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White else Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun generateNext21Days(): List<DayItem> {
    val calendar = Calendar.getInstance()
    val today = calendar.time

    val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    val weekdayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return (0..20).map { dayOffset ->
        calendar.time = today
        calendar.add(Calendar.DAY_OF_MONTH, dayOffset)
        val currentDate = calendar.time

        DayItem(
            date = currentDate,
            displayDay = dayFormat.format(currentDate),
            displayMonth = monthFormat.format(currentDate).uppercase(),
            displayWeekday = weekdayFormat.format(currentDate).uppercase(),
            dateString = apiDateFormat.format(currentDate),
            isToday = dayOffset == 0
        )
    }
}
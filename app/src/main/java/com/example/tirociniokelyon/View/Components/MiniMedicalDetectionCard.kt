package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

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
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.MonitorWeight
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection
import java.text.SimpleDateFormat
import java.util.Locale

data class DetectionAttributes(
    val icon: ImageVector,
    val color: Color,
    val label: String,
    val value: String
)

@Composable
fun MiniMedicalDetectionCard(medicalDetection: MedicalDetection, modifier: Modifier = Modifier) {


    val dayFormat = SimpleDateFormat("dd", Locale.ITALIAN)
    val monthFormat = SimpleDateFormat("MMM", Locale.ITALIAN)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.ITALIAN)

    val day = dayFormat.format(medicalDetection.date)
    val month = monthFormat.format(medicalDetection.date).uppercase()
    val time = timeFormat.format(medicalDetection.date)


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
                    .padding(top = 6.dp, start = 4.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                val detectionAttributes = when (medicalDetection.type) {
                    "SPO2" -> DetectionAttributes(
                        icon = Icons.Outlined.Favorite,
                        color = Color.Red,
                        label = "SPO2",
                        value = "${medicalDetection.value} %"
                    )

                    "HR" -> DetectionAttributes(
                        icon = Icons.Outlined.MonitorHeart,
                        color = Color.Red.copy(alpha = 0.7f),
                        label = "HR",
                        value = "${medicalDetection.value} bpm"
                    )

                    "Temperature" -> DetectionAttributes(
                        icon = Icons.Outlined.DeviceThermostat,
                        color = MaterialTheme.colorScheme.primary,
                        "Temperatura",
                        "${medicalDetection.value} °C"
                    )

                    else -> DetectionAttributes(
                        icon = Icons.Outlined.MonitorWeight,
                        color = Color(0xff00c853),
                        "Peso",
                        "${medicalDetection.value} kg"

                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Row (modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
                        Box(modifier = Modifier) {
                            Icon(
                                imageVector = detectionAttributes.icon,
                                contentDescription = null,
                                tint = detectionAttributes.color
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = detectionAttributes.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = detectionAttributes.value,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = "clock",
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }



                }

                // Time
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//
//
//
//
//                    }
//                }


            }
        }
    }


}
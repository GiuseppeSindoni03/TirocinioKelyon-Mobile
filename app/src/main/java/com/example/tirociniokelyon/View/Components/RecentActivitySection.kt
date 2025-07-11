package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components




import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Reservation



data class ActivityItem(
    val id: String = "",
    val icon: ImageVector,
    val title: String,
    val description: String,
    val titleColor: Color = Color.Black,
    val descriptionColor: Color = Color.Unspecified
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecentActivitiesSection(
    title: String = "Attività recenti",
    reservation: Reservation? = null,
    activities: List<ActivityItem> = emptyList(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    verticalSpacing: Dp = 16.dp,
    titleSpacing: Dp = 16.dp
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = title,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = contentPadding.calculateStartPadding(
                LayoutDirection.Ltr
            ))
        )

        Spacer(modifier = Modifier.height(titleSpacing))

        // LazyColumn con contenuto scrollabile
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            // Card della prenotazione (se presente)
            if (reservation != null) {
                item {
                    NextReservationCardHome(
                        reservation = reservation,
                    )
                }
            }

            // Lista delle attività
            items(activities) { activity ->
                GenericCard(
                    icon = activity.icon,
                    title = activity.title,
                    description = activity.description,
                    iconTint = MaterialTheme.colorScheme.primary,
                    titleColor = activity.titleColor,
                )
            }
        }
    }
}
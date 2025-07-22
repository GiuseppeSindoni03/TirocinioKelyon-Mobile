package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.MedicalDetection


fun labelType(type: String): String {
    if (type == "TEMPERATURE") return "TEMP"

    if( type == "WEIGHT") return "PESO"

    return type.uppercase()
}

fun labelView(view: String): String {
    return when (view) {
        "WEEK" -> "SETTIMANA"
        "MONTH" -> "MESE"
        else -> "3 MESI"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDetectionsList(
    detections: List<MedicalDetection>?,
    currentType: String = "ALL",
    ) {
        if (detections != null) {
            if (detections.isNotEmpty()) {
//                detections.forEach { detection ->
//                    MiniMedicalDetectionCard(medicalDetection = detection)
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(detections) { detection ->

                        MiniMedicalDetectionCard(medicalDetection = detection)
                    }
                }
            }  else {
                Log.d("DEBUG_LIST", "Showing EmptyAnimation for currentType: $currentType")
                val text = when (currentType) {
                    "SPO2" -> "Nessuna ossigenazione del sangue registrata."
                    "HR" -> "Nessun battito cardiaco registrato."
                    "TEMPERATURE" -> "Nessuna temperatura registrata."
                    "WEIGHT" -> "Nessun peso registrato."  // Correzione qui
                    else -> "Nessuna rilevazione registrata." // Migliorato per "ALL"
                }
                EmptyAnimation(text)
            }
        }

    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDetectionsHeader (
    onTypeChange: (String) -> Unit,
    onViewChange: (String) -> Unit,
    currentType: String = "ALL",
    currentView: String = "WEEK",
) {



    Header("Rilevazioni")

    Spacer(modifier = Modifier.height(16.dp))

    TypeFilterButtons(currentType = currentType, onTypeChange = onTypeChange)

    Spacer(modifier = Modifier.height(8.dp))

    ViewFilterButtons(currentView = currentView, onViewChange = onViewChange)

}

@Composable
private fun Header(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeFilterButtons(
    currentType: String,
    onTypeChange: (String) -> Unit
) {
    val types = remember {listOf(
        "ALL", "SPO2", "HR", "WEIGHT", "TEMPERATURE"
    ) }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),

        ) {




        types.forEachIndexed { index, type ->
            Log.d("DEBUG", "Rendering button: ($type)")

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = types.size,
                    baseShape = RoundedCornerShape(12.dp)
                ),

                onClick = { onTypeChange(type) },
                selected = labelType( currentType) == labelType(type),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,    // sfondo selezionato
                    activeContentColor = Color.White,           // testo selezionato
                    inactiveContainerColor = Color.White,
                    inactiveContentColor = Color.Black          // testo NON selezionato
                )
            ) {
                val buttonText = remember (type) { labelType((type)) }
                Text(
                    text = buttonText, style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  ViewFilterButtons (
    currentView: String,
    onViewChange: (String) -> Unit
)
{
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),

        ) {
        val views = remember {listOf(
            "WEEK", "MONTH", "3_MONTH",
        )}

        views.forEachIndexed { index, view ->
            Log.d("DEBUG", "Rendering button: ($view)")

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = views.size,
                    baseShape = RoundedCornerShape(12.dp)
                ),

                onClick = { onViewChange(view) },
                selected = labelView(currentView) == labelView(view),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,    // sfondo selezionato
                    activeContentColor = Color.White,           // testo selezionato
                    inactiveContainerColor = Color.White,
                    inactiveContentColor = Color.Black          // testo NON selezionato
                )
            ) {
                val buttonText = remember(view) { labelView(view) }
                Text(
                    text = buttonText, style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class InfoItemList(
    val name: String,
    val icon: ImageVector,
    val value: List<String>
)



@Composable
fun MedicalListInfo(items: List<InfoItemList>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Informazioni mediche dettagliate",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold
        )

        items.forEach { item ->
            MedicalListCard(item = item)
        }
    }
}

@Composable
fun MedicalListCard(item: InfoItemList) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con titolo e icona
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        modifier = Modifier.size(18.dp),
//                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }

            // Contenuto della lista
            if (item.value.isEmpty()) {

                    Text(
                        text = "Nessun dato disponibile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,

                    )

            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.value.forEachIndexed { index, value ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .padding(top = 6.dp)
                                    .background(
                                        color = Color.Black,
                                        shape = CircleShape
                                    )
                            )


                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }



        }
    }


// Componente di preview per testare il componente
@Preview(showBackground = true)
@Composable
fun MedicalListInfoPreview() {
    val sampleItems = listOf(
        InfoItemList(
            "Farmaci",
            Icons.Outlined.Medication,
            listOf("Aspirina 100mg", "Vitamina D3", "Omega 3")
        ),
        InfoItemList(
            "Patologie",
            Icons.Outlined.LocalHospital,
            listOf("Ipertensione", "Diabete tipo 2")
        ),
        InfoItemList(
            "Infortuni",
            Icons.Outlined.Add,
            emptyList()
        )
    )

    MedicalListInfo(items = sampleItems)
}

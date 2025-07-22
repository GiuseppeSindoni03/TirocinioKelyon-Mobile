package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.DetectionType
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.MedicalDetectionViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDetectionForm(onDismiss: () -> Unit) {

    val context = LocalContext.current

    val viewModel: MedicalDetectionViewModel = hiltViewModel()


    val state by viewModel.insertDetectionState.collectAsState()

    var expanded by remember {
        mutableStateOf(false)
    }


    var selectedType by remember { mutableStateOf(DetectionType.SPO2) }


    LaunchedEffect(selectedType) {
        Log.d("MedicalDetectionForm", "${selectedType.displayName} ${selectedType.realName}")
    }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth() // quasi full width
                    .fillMaxHeight(0.4f) // 75% dello schermo in altezza
                    .padding(4.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Aggiungi rilevazione",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        TextField(
                            readOnly = true,
                            value = state.selectedType.displayName,
                            onValueChange = {},
                            label = { Text("Tipo di rilevazione") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF0058CC),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = Color(0xFF0058CC)
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge

                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DetectionType.entries.forEach { detection ->
                                DropdownMenuItem(
                                    text = { Text(text = detection.displayName) },
                                    onClick = {
                                        viewModel.updateSelectedType(detection)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.value,
                        onValueChange = { viewModel.updateValue(it) },
                        placeholder = {
                            Text(
                                "${state.selectedType.placeholder} ${state.selectedType.unit}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
//                    leadingIcon = {
//                        Icon(
//                            Icons.Default.Email,
//                            contentDescription = "Email Icon",
//                            tint = Color(0xFF0058CC)
//                        )
//                    },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0058CC),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF0058CC)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp),
                        onClick = {
                            onDismiss()
                            viewModel.postMedicalDetection(
                                {
                                    Toast.makeText(
                                        context,
                                        "Rilevazione inserita con successo!",
                                        Toast.LENGTH_LONG
                                    ).show()


                                },
                                {
                                    Toast.makeText(
                                        context,
                                        "Errore durante l'inserimento!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                });
                            viewModel.resetInsertMedicalDetection()
                        }, shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(

                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,

                            ),
                    ) {
                        Text(text = "Invia", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

}


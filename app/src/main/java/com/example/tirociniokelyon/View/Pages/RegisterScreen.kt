package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.View.Components.AppTopBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.InviteViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.DTO.AcceptInviteRequest
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Invite

object RegisterColors {
    val Primary = Color(0xFF1976D2)
    val blue = Color(0xFF0058CC)
    val PrimaryLight = Color(0xFF42A5F5)
    val Surface = Color(0xFFFAFAFA)
    val SurfaceVariant = Color(0xFFF5F5F5)
    val OnSurface = Color(0xFF1C1C1C)
    val OnSurfaceVariant = Color(0xFF666666)
    val Error = Color(0xFFD32F2F)
    val Success = Color(0xFF388E3C)
    val CardReadOnly = Color(0xFFF8F9FA)
    val CardEditable = Color(0xFFFFFFFF)
    val Divider = Color(0xFFE0E0E0)
}

@Composable
fun RegisterScreen(
    navController: NavController,
    inviteId: String,
    viewModel: InviteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }


    // Inizializza i campi modificabili con i valori dell'invito
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var cap by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.acceptInviteSuccess) {
        if (uiState.acceptInviteSuccess == true) {
            Toast.makeText(
                context,
                "Registrazione completata! Effettua il login per continuare.",
                Toast.LENGTH_LONG
            ).show()

            navController.navigate("login") {
                popUpTo("register") { inclusive = true } // opzionale: rimuove register dallo stack
            }
        }

    }


    // Aggiorna i valori quando l'invito viene caricato
    LaunchedEffect(uiState.invite) {
        uiState.invite?.let { invite ->
            address = invite.address
            city = invite.city
            cap = invite.cap
            province = invite.province
        }
    }

    LaunchedEffect(inviteId) {
        Log.d("DEBUG", "Sono dentro Register Screen")
        viewModel.getInvite(inviteId)
    }


        when {
            uiState.isLoading -> {
                LoadingComponent ()
            }

            uiState.error != null -> {
                ErrorComponent(error = uiState.error.toString())
            }

            uiState.invite != null -> {
                Log.d("DEBUG", "Invito totale: ${uiState.invite}")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RegisterContent(
                        invite = uiState.invite!!,
                        address = address,
                        city = city,
                        cap = cap,
                        province = province,
                        password = password,
                        passwordVisible = passwordVisible,
                        onAddressChange = { address = it },
                        onCityChange = { city = it },
                        onCapChange = { cap = it },
                        onProvinceChange = { province = it },
                        onPasswordChange = { password = it },
                        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                        onRegisterClick = {
                            val acceptInviteRequest = AcceptInviteRequest(
                                name = uiState.invite!!.name,
                                surname = uiState.invite!!.surname,
                                email = uiState.invite!!.email,
                                cf = uiState.invite!!.cf,
                                birthDate = uiState.invite!!.birthDate,
                                gender = uiState.invite!!.gender,
                                phone = uiState.invite!!.phone,
                                address = address,
                                city = city,
                                cap = cap,
                                province = province,
                                password = password
                            )
                            viewModel.acceptInvite(inviteId, acceptInviteRequest)
                        },
                        isFormValid = address.isNotBlank() &&
                                city.isNotBlank() &&
                                cap.isNotBlank() &&
                                province.isNotBlank() &&
                                password.isNotBlank()
                    )
                }
                }

            }
        }


@Composable
private fun RegisterContent(
    invite: Invite,
    address: String,
    city: String,
    cap: String,
    province: String,
    password: String,
    passwordVisible: Boolean,
    onAddressChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCapChange: (String) -> Unit,
    onProvinceChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onRegisterClick: () -> Unit,
    isFormValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Completa la registrazione",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = RegisterColors.blue,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Verifica i tuoi dati e imposta una password",
            fontSize = 14.sp,
            color = RegisterColors.OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 32.dp)
        )

        // Sezione dati personali (non modificabili)
        PersonalDataSection(invite)

        Spacer(modifier = Modifier.height(24.dp))

        MedicalDataSection(invite = invite)

        Spacer(modifier = Modifier.height(24.dp))

        // Sezione dati indirizzo e password (modificabili)
        EditableDataSection(
            address = address,
            city = city,
            cap = cap,
            province = province,
            password = password,
            passwordVisible = passwordVisible,
            onAddressChange = onAddressChange,
            onCityChange = onCityChange,
            onCapChange = onCapChange,
            onProvinceChange = onProvinceChange,
            onPasswordChange = onPasswordChange,
            onPasswordVisibilityChange = onPasswordVisibilityChange
        )

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = onRegisterClick,
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RegisterColors.Primary,
                contentColor = Color.White,
                disabledContainerColor = RegisterColors.OnSurfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Completa registrazione",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PersonalDataSection(invite: Invite) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,

        ),
        border = BorderStroke(1.dp, RegisterColors.blue),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Dati personali",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RegisterColors.blue
                )
            }

            // Griglia di campi 2x2 per ottimizzare lo spazio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernReadOnlyTextField(
                    value = invite.name, // invite.name
                    label = "Nome",
                    modifier = Modifier.weight(1f)
                )
                ModernReadOnlyTextField(
                    value = invite.surname, // invite.surname
                    label = "Cognome",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ModernReadOnlyTextField(
                value = invite.email, // invite.email
                label = "Email",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernReadOnlyTextField(
                    value = invite.cf, // invite.cf
                    label = "Codice Fiscale",
                    modifier = Modifier.weight(1f)
                )
                ModernReadOnlyTextField(
                    value = invite.birthDate, // invite.birthDate
                    label = "Data di nascita",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun MedicalDataSection(invite: Invite) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, RegisterColors.blue),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Dati medici",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RegisterColors.blue
                )
            }

            // Griglia di campi 2x2 per ottimizzare lo spazio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernReadOnlyTextField(
                    value = invite.weight.toString(), // invite.name
                    label = "Peso",
                    modifier = Modifier.weight(1f)
                )
                ModernReadOnlyTextField(
                    value = invite.height.toString(), // invite.surname
                    label = "Altezza",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ModernReadOnlyTextField(
                value = invite.bloodType, // invite.email
                label = "Gruppo sanguigno",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernReadOnlyTextField(
                    value = invite.sport, // invite.cf
                    label = "Sport",
                    modifier = Modifier.weight(1f)
                )
                ModernReadOnlyTextField(
                    value = invite.level, // invite.birthDate
                    label = "Livello sportivo",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if(!invite.pathologies.isNullOrEmpty())
                MedicalNotesSection(title = "Patologie", items = invite.pathologies)

            Spacer(modifier = Modifier.height(8.dp))
            if(!invite.medications.isNullOrEmpty())
                MedicalNotesSection(title = "Farmaci", items = invite.medications)

            Spacer(modifier = Modifier.height(8.dp))
            if(!invite.injuries.isNullOrEmpty())
                 MedicalNotesSection(title = "Infortuni", items = invite.injuries)




        }
    }
}

@Composable
private fun MedicalNotesSection(
    title: String,
    items: List<String>
) {
    if (items.isEmpty()) return


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),

    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = RegisterColors.blue,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            var count = 1
            for (item in items) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    ModernReadOnlyTextField(value = item, label = "Patologia $count")

//                    Text("•", fontSize = 16.sp, modifier = Modifier.padding(end = 6.dp))
//                    Text(
//                        text = item,
//                        fontSize = 14.sp,
//                        color = RegisterColors.OnSurfaceVariant
//                    )
                }

                count  =  count + 1
            }
        }
    }
}


@Composable
private fun EditableDataSection(
    address: String,
    city: String,
    cap: String,
    province: String,
    password: String,
    passwordVisible: Boolean,
    onAddressChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCapChange: (String) -> Unit,
    onProvinceChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = RegisterColors.CardEditable
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modificabile",
                    tint = RegisterColors.Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Completa i tuoi dati",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RegisterColors.Primary
                )
            }

            // Indirizzo
            ModernTextField(
                value = address,
                onValueChange = onAddressChange,
                label = "Indirizzo",
                placeholder = "Inserisci il tuo indirizzo",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Città e CAP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernTextField(
                    value = city,
                    onValueChange = onCityChange,
                    label = "Città",
                    placeholder = "Città",
                    modifier = Modifier.weight(1f)
                )
                ModernTextField(
                    value = cap,
                    onValueChange = onCapChange,
                    label = "CAP",
                    placeholder = "00000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Provincia
            ModernTextField(
                value = province,
                onValueChange = onProvinceChange,
                label = "Provincia",
                placeholder = "Provincia",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            ModernTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Crea una password sicura",
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityChange) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password",
                            tint = RegisterColors.OnSurfaceVariant
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}





@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontSize = 14.sp,
                color = RegisterColors.OnSurfaceVariant
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 14.sp,
                color = RegisterColors.OnSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = RegisterColors.Primary,
            focusedLabelColor = RegisterColors.Primary,
            unfocusedBorderColor = RegisterColors.OnSurfaceVariant.copy(alpha = 0.3f),
            unfocusedLabelColor = RegisterColors.OnSurfaceVariant,
            cursorColor = RegisterColors.Primary
        ),
        singleLine = true
    )
}
@Composable
private fun ModernReadOnlyTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                color = RegisterColors.OnSurfaceVariant
            )
        },
        enabled = false,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = RegisterColors.OnSurface,
            disabledLabelColor = RegisterColors.OnSurfaceVariant,
            disabledBorderColor = RegisterColors.OnSurfaceVariant.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}
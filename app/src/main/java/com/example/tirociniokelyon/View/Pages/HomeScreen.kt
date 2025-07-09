package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages


import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton



import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tirociniokelyon.R

import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetEdgeToEdgeSystemBars
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetSystemBarStyle
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.HomeViewModel
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Doctor
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.Patient
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.User
import com.example.tirociniokelyon.com.example.tirociniokelyon.model.UserDoctor
import com.example.tirociniokelyon.ui.theme.TirocinioKelyonTheme

import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController,

               )  {

    SetSystemBarStyle(statusBarColor = Color.Transparent, darkIcons = true)
    SetEdgeToEdgeSystemBars(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        darkIcons = true
    )

    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.currentUser.collectAsState()


    LaunchedEffect(Unit) {
        Log.d("DEBUG", "Sondo nella home page")
    }


    Scaffold (
        topBar = {
                 Column (
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(top = 80.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                     horizontalAlignment = Alignment.Start,
                     verticalArrangement = Arrangement.Top

                 ) {
                     val now = LocalDateTime.now()
                     val hour = now.hour

                     val saluto = when (hour) {
                         in 5..11 -> "Buongiorno,"
                         in 12..17 -> "Buon pomeriggio,"
                         else -> "Buonasera,"
                     }

                     Text(text = "$saluto ${user?.name}", modifier = Modifier
                         .padding(top = 8.dp),
                         style = MaterialTheme.typography.displayLarge,
                         color = Color.Black,
                     )
                     Text(text = "Come ti senti oggi?", modifier = Modifier.padding(top= 4.dp),
                         style = MaterialTheme.typography.bodyLarge,
                         color = Color.Gray,
                         )


                     Log.d("USER", "$user")


                 }
        },
        bottomBar = {
            NavBar(navController = navController) }
    ) {
        paddingValues ->
        Box  (
            modifier =  Modifier
                .padding(paddingValues)
        ){

            when {
                uiState.isLoading ->
                    LoadingComponent()

                uiState.error != null ->
                    ErrorComponent(error = uiState.error.toString())

                uiState.doctor != null -> {
                   DoctorInfoCard(doctor = uiState.doctor!!)

                }
            }

        }
    }



}


@Composable
fun DoctorInfoCard(doctor: Doctor) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header del dottore
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto profilo
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Se hai un'immagine del dottore, sostituisci con AsyncImage
                        Image(
                            painter = painterResource(id = R.drawable.doctor),
                            contentDescription = "Logo SymbioCare",
                        )                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Dr. ${doctor.user.name} ${doctor.user.surname}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    doctor.specialization?.let { specialization ->
                        Text(
                            text = specialization,
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    doctor.user.email?.let { email ->
                        Text(
                            text = email,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


                // Pulsante Chiama
                doctor.user.phone.let { phone ->
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary

                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chiama",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }

            }
        }
    }



@Preview(showBackground = true)
@Composable
fun DoctorInfoCardPreview() {
    val fakeDoctor = Doctor(
        user = UserDoctor(
            id = "1",
            name = "Alessandro",
            surname = "Rossi",
            email = "alessandro.rossi@clinicaitalia.it",
            birthDate = "1980-04-22",
            cf = "RSSLSN80D22F205X",
            gender = "M",
            phone = "+393331234567",
            role = "DOCTOR",
            address = "Via delle Mimose 12",
            city = "Napoli",
            cap = "80100",
            province = "NA",
        ),
        specialization = "Chirurgia Generale",
        medicalOffice =  "ciao",
        orderDate = "",
        orderProvince = "",
        orderType = "",
        registrationNumber = "",
        userId = ""
    )

    TirocinioKelyonTheme {
        DoctorInfoCard(doctor = fakeDoctor)
    }
}

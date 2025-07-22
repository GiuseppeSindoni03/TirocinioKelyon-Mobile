package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Blind
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonalInjury
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.InfoItem
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.InfoItemList
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.Infos
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.MedicalListInfo
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.AuthViewModel


@Composable
fun UserProfile(navController: NavController) {

    val viewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current

    val user by viewModel.currentUser.collectAsState()

    Log.d("USER", "$user")

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
                    text = "Profilo utente",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
            }
        },
        bottomBar = {


            NavBar(navController = navController)

        },
    ) { paddingValues ->
        if (user == null) {
            LoadingComponent()
            return@Scaffold
        }
        val personalInfoItems = listOf(
            InfoItem("Email", Icons.Outlined.Email, user!!.email),
            InfoItem("Telefono", Icons.Outlined.Phone, user!!.phone),
            InfoItem("Genere", Icons.Outlined.Male, user!!.gender),
            InfoItem("Città", Icons.Outlined.LocationCity, user!!.city),
            InfoItem("Indirizzo", Icons.Outlined.Home, user!!.address),
            InfoItem("CAP", Icons.Outlined.LocationOn, user!!.cap)
        )

        val medicalInfoItems = listOf(
            InfoItem("Peso", Icons.Outlined.MonitorWeight, "${user!!.patient.weight} kg"),
            InfoItem("Altezza", Icons.Outlined.Height, "${user!!.patient.height} cm"),
            InfoItem("Sport", Icons.Outlined.FitnessCenter, user!!.patient.sport),
            InfoItem("Livello", Icons.Outlined.FitnessCenter, user!!.patient.level),
            InfoItem("Gruppo sanguigno", Icons.Outlined.WaterDrop, user!!.patient.bloodType)
        )

        val medicalListItems = listOf(
            InfoItemList("Farmaci", Icons.Outlined.HealthAndSafety, user!!.patient.medications),
            InfoItemList("Patologie", Icons.Outlined.Blind, user!!.patient.pathologies),
            InfoItemList("Infortuni", Icons.Outlined.PersonalInjury, user!!.patient.injuries)
        )


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {

            item {
                UserProfileHeader(name = user!!.name, surname = user!!.surname, cf = user!!.cf, onLogout = {                viewModel.logout {
                    Log.d("DEBUG", "logout effettuato")
                    navController.navigate("login")
                }
                    Toast.makeText(
                        context,
                        "Logout effettuato. Alla prossima!!",
                        Toast.LENGTH_LONG
                    ).show()})

            }

            item {

                Infos(personalInfoItems, "Informazioni personali")
            }

            item {
                Infos(medicalInfoItems, "Informazioni mediche")
            }

            item {
                MedicalListInfo(medicalListItems)
            }




        }

    }


}

@Composable
fun UserProfileHeader(name: String, surname: String, cf: String, onLogout: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Card(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Box(
                modifier = Modifier.fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(80.dp)
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$name $surname",
            style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "$cf",
            style = MaterialTheme.typography.bodyLarge,
//                fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick =
                onLogout

            ,
            modifier = Modifier

                .width(120.dp)
                .padding(horizontal = 16.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(text = "Esci", style = MaterialTheme.typography.titleMedium)
        }
    }
}
//}


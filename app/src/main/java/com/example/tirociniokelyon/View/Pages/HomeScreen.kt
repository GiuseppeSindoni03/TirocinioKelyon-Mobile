package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages


import java.time.LocalDateTime

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite

import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.outlined.FitnessCenter

import androidx.compose.material3.MaterialTheme


import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ActivityItem

import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.DoctorInfoCard

import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ErrorComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.NavBar
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.RecentActivitiesSection
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetEdgeToEdgeSystemBars
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.SetSystemBarStyle
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.ShortCuts
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.HomeViewModel



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,

    ) {

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


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
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

                Text(
                    text = "$saluto ${user?.name}",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.Black,
                )
                Text(
                    text = "Come ti senti oggi?", modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )


                Log.d("USER", "$user")


            }
        },
        bottomBar = {
            NavBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {

            when {
                uiState.isLoading ->
                    LoadingComponent()

                uiState.error != null ->
                    ErrorComponent(error = uiState.error.toString())


                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        DoctorInfoCard(doctor = uiState.doctor!!)

                        Spacer(modifier = Modifier.height(8.dp))

                        ShortCuts()

                        Spacer(modifier = Modifier.height(8.dp))

                        val activities = listOf(
                            ActivityItemFactory.createBloodPressureActivity(),
                            ActivityItemFactory.createWeightActivity(),
                            ActivityItemFactory.createHeartRateActivity(),

                        )

                       RecentActivitiesSection(
                           reservation = uiState.reservation,
                           activities = activities
                       )

                    }


                }


            }
        }


    }
}

object ActivityItemFactory {
    fun createBloodPressureActivity(
        systolic: Int = 120,
        diastolic: Int = 80,
        status: String = "Normale"
    ) = ActivityItem(
        id = "blood_pressure",
        icon = Icons.Filled.MonitorHeart,
        title = "Pressione registrata",
        description = "$systolic/$diastolic mmHg - $status"
    )

    fun createHeartRateActivity(
        bpm: Int = 72,
        status: String = "Normale"
    ) = ActivityItem(
        id = "heart_rate",
        icon = Icons.Default.Favorite,
        title = "Frequenza cardiaca",
        description = "$bpm bpm - $status"
    )

    fun createWeightActivity(
        weight: Double = 70.5,
        unit: String = "kg"
    ) = ActivityItem(
        id = "weight",
        icon = Icons.Outlined.FitnessCenter,
        title = "Peso registrato",
        description = "$weight $unit"
    )


}

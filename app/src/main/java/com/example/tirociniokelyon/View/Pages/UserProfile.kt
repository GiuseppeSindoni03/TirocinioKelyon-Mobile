package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.ViewModel.AuthViewModel

@Composable
fun UserProfile (navController: NavController) {

    val viewModel: AuthViewModel =  hiltViewModel()
    val context = LocalContext.current

    Column (modifier = Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { viewModel.logout {
            Log.d("DEBUG", "logout effettuato")
            navController.navigate("login") }
            Toast.makeText(
                context,
                "Logout effettuato. Alla prossima!!",
                Toast.LENGTH_LONG
            ).show()

        }) {
            Text(text = "Logout")
        }
    }
}
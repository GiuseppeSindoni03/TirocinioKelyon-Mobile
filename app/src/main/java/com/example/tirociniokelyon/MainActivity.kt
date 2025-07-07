package com.example.tirociniokelyon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.HomeScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.InviteQRScannerBottomSheet
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.LoginScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.RegisterScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.UserProfile
import com.example.tirociniokelyon.ui.theme.TirocinioKelyonTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TirocinioKelyonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp () {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "login" ) {
        composable("login") {
            LoginScreen(navController = navController)
        }


        composable("home") {
            HomeScreen(navController,)
        }
        
        composable("user-profile") {
            UserProfile(navController = navController)
        }



        composable(
            route = "register/{inviteId}",
            arguments = listOf(navArgument("inviteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val inviteId = backStackEntry.arguments?.getString("inviteId") ?: ""
            Log.d("DEBUG", "ID PASSATO a RegisterScreen $inviteId")
            RegisterScreen(navController = navController, inviteId = inviteId)
        }

        composable(
            route= "insert-medical"

        ) {
            HomeScreen(navController = navController)
        }


        composable(
            route= "medical-detection"

        ) {
            HomeScreen(navController = navController)
        }


        composable(
            route= "reservation"

        ) {
            HomeScreen(navController = navController)
        }


    }
}



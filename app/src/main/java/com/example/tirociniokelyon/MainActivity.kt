package com.example.tirociniokelyon

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.AddReservationScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.HomeScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.InviteQRScannerBottomSheet
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.LoginScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.RegisterScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.ReservationScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.UserProfile
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager
import com.example.tirociniokelyon.ui.theme.TirocinioKelyonTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TirocinioKelyonTheme(dynamicColor = false) {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(userSessionManager, forceLogin = false)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(userSessionManager: UserSessionManager, forceLogin: Boolean = false) {
    val navController = rememberNavController()


    var startDestination by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }



    LaunchedEffect(forceLogin) {
        if (forceLogin) {
            userSessionManager.clearUser()
            startDestination = "login"
            isLoading = false
        } else {
            val isValidSession = userSessionManager.validateSession()
            startDestination = if (isValidSession) "home" else "login"
            isLoading = false
        }
    }

    if (isLoading || startDestination == null) {
        LoadingComponent()
    } else {

        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("login") {
                LoginScreen(navController = navController)
            }


            composable("home") {
                HomeScreen(navController)
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
                route = "insert-medical"

            ) {
                HomeScreen(navController = navController)
            }


            navigation(
                startDestination =" medical-detection/list",
                route = "medical-detection"

            ) {
                composable("medical-detection/list") {

                    HomeScreen(navController = navController)

                }



            }


            navigation(startDestination ="reservation/list"
,                route = "reservation"

            ) {
                composable("reservation/list") {
                    ReservationScreen(navController = navController)

                }

                composable("reservation/add" ) {
                    AddReservationScreen(navController = navController)
                }
            }




        }

    }
}



package com.example.tirociniokelyon

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components.LoadingComponent
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.AddReservationScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.HomeScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.LoginScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.MedicalDetectionScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.RegisterScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.ReservationScreen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.SpO2Screen
import com.example.tirociniokelyon.com.example.tirociniokelyon.View.Pages.UserProfile
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.UserSessionManager
import com.example.tirociniokelyon.ui.theme.TirocinioKelyonTheme
import com.example.tirociniokelyon.utils.BleConnector
import com.example.tirociniokelyon.utils.BluetoothManagerSingleton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userSessionManager: UserSessionManager

    // Registriamo i launcher PRIMA del ciclo di vita
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // IMPORTANTE: Registriamo i launcher prima di tutto
        setupLaunchers()

        // Inizializza il BluetoothManager
        val bluetoothManager = BluetoothManagerSingleton.getInstance()
        bluetoothManager.initialize(this)

        setContent {
            TirocinioKelyonTheme(dynamicColor = false) {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(userSessionManager, forceLogin = false, activity = this@MainActivity, bluetoothManager = bluetoothManager)
                }
            }
        }
    }

    private fun setupLaunchers() {
        // Launcher per i permessi
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            BluetoothManagerSingleton.getInstance().onPermissionsResult(permissions)
        }

        // Launcher per l'attivazione Bluetooth
        bluetoothEnableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val isEnabled = result.resultCode == RESULT_OK
            BluetoothManagerSingleton.getInstance().onBluetoothEnableResult(isEnabled)
        }
    }
    fun requestPermissions() {
        BluetoothManagerSingleton.getInstance().requestPermissions(permissionLauncher)
    }

    fun requestBluetoothEnable() {
        BluetoothManagerSingleton.getInstance().requestBluetoothEnable(bluetoothEnableLauncher)
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(userSessionManager: UserSessionManager, forceLogin: Boolean = false, activity: MainActivity,  bluetoothManager: BluetoothManagerSingleton) {

    val navController = rememberNavController()


    var startDestination by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Osserviamo lo stato del Bluetooth
    val isBluetoothReady by bluetoothManager.isReady.collectAsState()
    val hasPermissions by bluetoothManager.hasPermissions.collectAsState()
    val isBluetoothEnabled by bluetoothManager.isBluetoothEnabled.collectAsState()

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

    LaunchedEffect(hasPermissions, isBluetoothEnabled) {
        if (!hasPermissions) {
            activity.requestPermissions()
        } else if (!isBluetoothEnabled) {
            activity.requestBluetoothEnable( )
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
                startDestination = "reservation/list", route = "reservation"

            ) {
                composable("reservation/list") {
                    ReservationScreen(navController = navController)

                }

                composable("reservation/add") {
                    AddReservationScreen(navController = navController)
                }
            }



            navigation(
                startDestination = "medical-detection/list",
                route = "medical-detection"
            ) {
                composable("medical-detection/list") {
                    MedicalDetectionScreen(navController)
                }

                composable("medical-detection/spo2-test") {
                    SpO2Screen(activity = activity, navController = navController)
                }
            }


        }

    }
}



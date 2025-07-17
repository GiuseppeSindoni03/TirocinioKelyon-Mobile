package com.example.tirociniokelyon.com.example.tirociniokelyon.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val BLUETOOTH_PERMISSION_REQUEST_CODE = 101
    const val LOCATION_PERMISSION_REQUEST_CODE = 102


    // Definisco i permessi necessari in base alla versione
    val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    fun hasBluetoothPermissions(activity: Context): Boolean {
        return bluetoothPermissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasLocationPermissions(context: Context): Boolean {
        return locationPermissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Verifica se un singolo permesso è concesso
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Richiede i permessi Bluetooth
     */
    fun requestBluetoothPermissions(activity: Activity, requestCode: Int = BLUETOOTH_PERMISSION_REQUEST_CODE) {
        ActivityCompat.requestPermissions(activity, bluetoothPermissions, requestCode)
    }

    /**
     * Richiede i permessi di localizzazione
     */
    fun requestLocationPermissions(activity: Activity, requestCode: Int = LOCATION_PERMISSION_REQUEST_CODE) {
        ActivityCompat.requestPermissions(activity, locationPermissions, requestCode)
    }

    /**
     * Richiede un singolo permesso
     */
    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    /**
     * Verifica se tutti i permessi sono stati concessi dal risultato della richiesta
     */
    fun arePermissionsGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }

    /**
     * Verifica se dovremmo mostrare la spiegazione per i permessi
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Ottiene i permessi mancanti per il Bluetooth
     */
    fun getMissingBluetoothPermissions(context: Context): List<String> {
        return bluetoothPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Ottiene i permessi mancanti per la localizzazione
     */
    fun getMissingLocationPermissions(context: Context): List<String> {
        return locationPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Verifica se tutti i permessi necessari per il BLE sono concessi
     */
    fun hasAllBlePermissions(context: Context): Boolean {
        return hasBluetoothPermissions(context) && hasLocationPermissions(context)
    }

    /**
     * Richiede tutti i permessi necessari per il BLE
     */
    fun requestAllBlePermissions(activity: Activity, requestCode: Int = BLUETOOTH_PERMISSION_REQUEST_CODE) {
        val allPermissions = bluetoothPermissions + locationPermissions
        val uniquePermissions = allPermissions.distinct().toTypedArray()
        ActivityCompat.requestPermissions(activity, uniquePermissions, requestCode)
    }
}

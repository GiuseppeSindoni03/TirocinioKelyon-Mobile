package com.example.tirociniokelyon.com.example.tirociniokelyon.utils

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionManager {
    companion object {
        const val REQUEST_CODE_ALL_PERMISSIONS = 311
        const val REQUEST_CODE_LOCATION = 411
        const val REQUEST_CODE_GET_BLUETOOTH_LIST = 412
        const val REQUEST_CODE_CAMERA = 511
        const val PERMISSION_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val PERMISSION_LOCATION_Q = Manifest.permission.ACCESS_FINE_LOCATION

        fun isObtain(activity: Activity, permission: String, requestCode: Int): Boolean {
            return if (isAboveAndroidOS6_0()) {
                val checkPermission = ActivityCompat.checkSelfPermission(activity, permission)
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
                    false
                } else {
                    true
                }
            } else {
                true
            }
        }

        fun isObtain(fragment: Fragment, permission: String, requestCode: Int): Boolean {
            return if (isAboveAndroidOS6_0()) {
                val context = fragment.context ?: return false
                val checkPermission = ActivityCompat.checkSelfPermission(context, permission)
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    fragment.requestPermissions(arrayOf(permission), requestCode)
                    false
                } else {
                    true
                }
            } else {
                true
            }
        }

        fun isObtain(activity: Activity, permissions: Array<String>, requestCode: Int): Boolean {
            return if (isAboveAndroidOS6_0()) {
                val permissionsList = mutableListOf<String>()

                permissions.forEach { permission ->
                    val checkPermission = ActivityCompat.checkSelfPermission(activity, permission)
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        permissionsList.add(permission)
                    }
                }

                when {
                    permissionsList.isEmpty() -> true
                    permissionsList.size == permissions.size -> {
                        ActivityCompat.requestPermissions(activity, permissions, requestCode)
                        false
                    }
                    else -> {
                        val newPermissions = permissionsList.toTypedArray()
                        ActivityCompat.requestPermissions(activity, newPermissions, requestCode)
                        false
                    }
                }
            } else {
                true
            }
        }

        fun isPermissionGranted(
            activity: Activity,
            permission: String,
            dialogMsg: String,
            requestCode: Int
        ): Boolean {
            return if (isAboveAndroidOS6_0()) {
                val hasWritePermission = ActivityCompat.checkSelfPermission(activity, permission)
                if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                        showMessageOKCancel(activity, dialogMsg) { _, _ ->
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", activity.application.packageName, null)
                                intent.data = uri
                                activity.startActivityForResult(intent, 101)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        false
                    } else {
                        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
                        false
                    }
                } else {
                    true
                }
            } else {
                true
            }
        }

        private fun showMessageOKCancel(
            activity: Activity,
            message: String,
            okListener: DialogInterface.OnClickListener
        ) {
            androidx.appcompat.app.AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("Concessione manuale", okListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show()
        }

        fun isPermissionGranted(grantResults: IntArray): Boolean {
            return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        }

        fun getUseBluetoothPermissions(): Array<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }


        fun getAllPermissions(): Array<String> {
            return arrayOf(
                // location (for bluetooth scan)
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                // Camera(QR code scan)
                Manifest.permission.CAMERA,
                // Phone
                Manifest.permission.READ_PHONE_STATE,
                // Read contact
                Manifest.permission.READ_CONTACTS,
                // SD-Card
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                // Microphone
                Manifest.permission.RECORD_AUDIO
            )
        }

        fun canScanBluetoothDevice(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= 23) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } else {
                true
            }
        }

        fun openGPS(activity: Activity) {
            val intent = Intent().apply {
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            try {
                activity.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                intent.action = Settings.ACTION_SETTINGS
                try {
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Can not find the GPS setting page.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun isAboveAndroidOS6_0(): Boolean {
            return Build.VERSION.SDK_INT >= 23
        }

        fun getBluetoothPermissions(): Array<String> {
            return arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }
}
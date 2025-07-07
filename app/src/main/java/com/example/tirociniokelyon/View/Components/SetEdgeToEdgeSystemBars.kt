package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetEdgeToEdgeSystemBars(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    darkIcons: Boolean = true
) {
    val view = LocalView.current
    val activity = view.context as Activity

    SideEffect {
        val window = activity.window

        // Permette ai contenuti di disegnare sotto status e nav bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Colore status bar e navigation bar trasparenti
        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()

        // Imposta icone scure o chiare
        val insetsController = WindowInsetsControllerCompat(window, view)
        insetsController.isAppearanceLightStatusBars = darkIcons
        insetsController.isAppearanceLightNavigationBars = darkIcons
    }
}

package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import androidx.core.view.WindowCompat
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetSystemBarStyle(
    statusBarColor: Color = Color.Transparent,
    darkIcons: Boolean = true
) {
    val view = LocalView.current
    val activity = view.context as Activity

    SideEffect {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false) // per edge-to-edge
        window.statusBarColor = statusBarColor.toArgb()

        val insetsController = WindowInsetsControllerCompat(window, view)
        insetsController.isAppearanceLightStatusBars = darkIcons
    }
}

package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem


data class NavItem(
    val route: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector
)




@Composable
fun NavBar(navController: NavController) {




    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val items = listOf(
        NavItem("home", Icons.Outlined.Dashboard, Icons.Filled.Home),
        NavItem("reservation", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
        NavItem("medical-detection", Icons.Outlined.MonitorHeart, Icons.Filled.MonitorHeart),
        NavItem("user-profile", Icons.Outlined.Person, Icons.Filled.Person),

    )

    Box {
        NavigationBar(
            containerColor = Color(0xFF0058CC),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(max = 60.dp)
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconFilled else item.iconOutlined,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.White.copy(alpha = 1f),
                        unselectedTextColor = Color.White.copy(alpha = 1f)
                    )
                )
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("insert-medical") },
            containerColor = Color(0xFF0058CC),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Add")
        }
    }
}

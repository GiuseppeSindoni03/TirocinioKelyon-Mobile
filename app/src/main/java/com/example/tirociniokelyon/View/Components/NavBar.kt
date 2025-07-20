package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PermissionUtils


data class NavItem(
    val route: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector
)


@Composable
fun NavBar(navController: NavController) {
    val context = LocalContext.current

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Log.d("DEBUG", "Current Route: $currentRoute")

    val items = listOf(
        NavItem("home", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem("reservation/list", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
        NavItem("medical-detection/list", Icons.Outlined.MonitorHeart, Icons.Filled.MonitorHeart),
        NavItem("user-profile", Icons.Outlined.Person, Icons.Filled.Person),

        )

    val fabScreens = listOf("reservation/list", "medical-detection/list")
    val showFab = currentRoute in fabScreens

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp,  bottom = 40.dp),
        contentAlignment = Alignment.Center

    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.secondary,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ).align(Alignment.Center),



        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    modifier = Modifier.padding(top = 8.dp),
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
                            modifier = Modifier.size(26.dp)
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

        if (showFab) {
            FloatingActionButton(
                onClick = {
                    when (currentRoute) {
                        "reservation/list" -> navController.navigate("reservation/add")
                        "medical-detection/list" ->

                            if (PermissionUtils.hasBluetoothPermissions(context)) {
                                navController.navigate("medical-detection/spo2-test")
                            }


                    }
                },


                containerColor = MaterialTheme.colorScheme.secondary,
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
}

@Preview(showBackground = false, name = "NavBar Preview")
@Composable
fun NavBarPreview() {
    val navController = rememberNavController()

    // Finta composable per test visivo
    Box(modifier = Modifier.heightIn(max = 100.dp)) {
        NavBar(navController = navController)
    }
}
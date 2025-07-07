package com.example.tirociniokelyon.View.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Definisci i colori come costanti per riutilizzabilità
object AppColors {
    val Primary = Color(0xFF0058CC)
    val OnPrimary = Color.White
    val OnSurface = Color.Black
    val Surface = Color.White
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    text: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    navigateTo: String? = null,
    onBackClick: (() -> Unit)? = null,
    backgroundColor: Color = AppColors.Surface,
    contentColor: Color = AppColors.OnSurface,
    backButtonColor: Color = AppColors.Primary,
    backButtonIcon: ImageVector = Icons.Filled.ArrowBack,
    actions: @Composable (RowScope.() -> Unit)? = null,
    centerTitle: Boolean = false
) {
    if (centerTitle) {
        // Versione con titolo centrato usando CenterAlignedTopAppBar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = text,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(
                        onClick = {
                            when {
                                onBackClick != null -> onBackClick()
                                navigateTo != null -> navController.navigate(navigateTo)
                                else -> navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = backButtonIcon,
                            contentDescription = "Indietro",
                            tint = backButtonColor
                        )
                    }
                }
            },
            actions = actions ?: {},
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = contentColor,
                navigationIconContentColor = backButtonColor
            ),
            modifier = modifier
        )
    } else {
        // Versione standard con titolo allineato a sinistra
        TopAppBar(
            title = {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(
                        onClick = {
                            when {
                                onBackClick != null -> onBackClick()
                                navigateTo != null -> navController.navigate(navigateTo)
                                else -> navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = backButtonIcon,
                            contentDescription = "Indietro",
                            tint = backButtonColor
                        )
                    }
                }
            },
            actions = actions ?: {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
                titleContentColor = contentColor,
                navigationIconContentColor = backButtonColor
            ),
            modifier = modifier
        )
    }
}

// Versione alternativa più personalizzata se preferisci il controllo completo
@Composable
fun CustomAppTopBar(
    text: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    navigateTo: String? = null,
    onBackClick: (() -> Unit)? = null,
    backgroundColor: Color = AppColors.Surface,
    contentColor: Color = AppColors.OnSurface,
    backButtonColor: Color = AppColors.Primary,
    backButtonIcon: ImageVector = Icons.Filled.ArrowBack,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Surface(
        color = backgroundColor,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Altezza standard per le top bar
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation Icon
            if (showBackButton) {
                IconButton(
                    onClick = {
                        when {
                            onBackClick != null -> onBackClick()
                            navigateTo != null -> navController.navigate(navigateTo)
                            else -> navController.popBackStack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = backButtonIcon,
                        contentDescription = "Indietro",
                        tint = backButtonColor
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp)) // Spazio equivalente a IconButton
            }

            // Title
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            // Actions
            if (actions != null) {
                Row(content = actions)
            }
        }
    }
}

// Preview per testare il componente
@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    MaterialTheme {
        Column {
            // TopBar con titolo centrato
            AppTopBar(
                text = "Registrazione",
                navController = rememberNavController(),
                showBackButton = true,
                centerTitle = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TopBar standard (titolo a sinistra)
            AppTopBar(
                text = "Titolo Standard",
                navController = rememberNavController(),
                showBackButton = true,
                centerTitle = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TopBar centrato con actions
            AppTopBar(
                text = "Titolo Centrato",
                navController = rememberNavController(),
                showBackButton = true,
                centerTitle = true,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Impostazioni"
                        )
                    }
                }
            )
        }
    }
}
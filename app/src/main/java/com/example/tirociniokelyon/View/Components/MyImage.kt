package com.example.tirociniokelyon.com.example.tirociniokelyon.View.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tirociniokelyon.R

@Composable
fun MyImage(size: Dp = 200.dp) {
    Image(
        painter = painterResource(id = R.drawable.linktop),
        contentDescription = "Descrizione dell'immagine",
        modifier = Modifier.size(size),
        contentScale = ContentScale.Crop
    )
}
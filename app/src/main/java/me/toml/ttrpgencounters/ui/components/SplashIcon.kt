package me.toml.ttrpgencounters.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import me.toml.ttrpgencounters.R

// I'm just doing this because I'm too lazy to open photoshop :P
@Composable
fun SplashIcon() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Easy", style = MaterialTheme.typography.h2)
        Box(modifier = Modifier) {
            Image(
                painter = painterResource(id = R.drawable.sword_icon_32134),
                contentDescription = "Sword Icon"
            )
            Column(
                modifier = Modifier
                    .matchParentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EZ",
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(text = "Encounters", style = MaterialTheme.typography.h2)
    }
}

@Preview
@Composable
fun SplaschIconPreview() {
    SplashIcon()
}
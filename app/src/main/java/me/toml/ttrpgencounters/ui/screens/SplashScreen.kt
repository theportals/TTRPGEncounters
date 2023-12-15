package me.toml.ttrpgencounters.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import me.toml.ttrpgencounters.ui.components.SplashIcon
import me.toml.ttrpgencounters.ui.navigation.Routes
import me.toml.ttrpgencounters.ui.viewmodels.SplashScreenViewModel

@Composable
fun SplashScreen(navHostController: NavHostController) {
    val viewModel: SplashScreenViewModel = viewModel()
    LaunchedEffect(true) {
        delay(1000)
        navHostController.navigate(
            if (viewModel.isUserLoggedIn()) {
                Routes.encountersNavigation.route
            } else {
                Routes.launchNavigation.route
            }
        ) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }

    // TODO: Beautify the splash screen, maybe add loading animation?
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SplashIcon()
        Text(
            text = "Tom Longhurst",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
        )
    }

}
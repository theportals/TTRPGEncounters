package me.toml.ttrpgencounters.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.toml.ttrpgencounters.ui.navigation.Routes

@Composable
fun LaunchScreen(navHostController: NavHostController) {
    // TODO: Beautify launch screen
    // TODO: next time, also add ads and make colors not default?
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.h2,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Easy Encounters is a lightweight, easy-to-use encounter management app for TTRPGs",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Please create an account so we can store your encounters",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navHostController.navigate(Routes.signUp.route) }) {
                Text(text = "Create Account")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an account?")
                TextButton(onClick = { navHostController.navigate(Routes.signIn.route) }) {
                    Text(text = "Sign in")
                }
            }
        }
    }
}
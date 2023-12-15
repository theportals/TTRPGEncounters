package me.toml.ttrpgencounters.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.toml.ttrpgencounters.ui.navigation.RootNavigation
import me.toml.ttrpgencounters.ui.theme.TTRPGEncountersTheme

@Composable
fun App() {
    TTRPGEncountersTheme {
        RootNavigation()
    }
}
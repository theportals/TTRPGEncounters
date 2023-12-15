package me.toml.ttrpgencounters.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.ui.components.EncounterListItem
import me.toml.ttrpgencounters.ui.components.Loader
import me.toml.ttrpgencounters.ui.viewmodels.EncountersViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EncountersScreen(navHostController: NavHostController) {
    val viewModel: EncountersViewModel = viewModel()
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        val loadingEncounters = async {
            viewModel.getEncounters()
        }
        delay(2000)
        loadingEncounters.await()
        state.loading = false
    }

    if (state.loading) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Loader()
        }
    } else {

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            items(state.encounters, key = { it.id!! }) { encounter ->
                Row(modifier = Modifier.animateItemPlacement()) {
                    EncounterListItem(
                        encounter = encounter,
                        onEditPressed = {
                            navHostController.navigate("editencounter?id=${encounter.id}")
                        },
                        onDeletePressed = {
                            state.toDelete = encounter
                            state.deleting = true
                        },
                        onRunPressed = {
                            navHostController.navigate("runencounter?id=${encounter.id}")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    if (state.deleting) {
        AlertDialog(
            onDismissRequest = { state.deleting = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text(text = "Are you sure you wish to delete ${state.toDelete.title}?") },
            dismissButton = {
                Button(
                    onClick = {
                        state.deleting = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.surface)
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.deleteEncounter()
                        }
                        state.deleting = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error)
                ) {
                    Text(text = "Delete")
                }
            },
        )
    }
}
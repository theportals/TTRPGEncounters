package me.toml.ttrpgencounters.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.ui.components.CharacterListItem
import me.toml.ttrpgencounters.ui.models.Toon
import me.toml.ttrpgencounters.ui.viewmodels.CharactersViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharactersScreen(navHostController: NavHostController) {
    val viewModel: CharactersViewModel = viewModel()
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        val loadingEncounters = async {
            viewModel.getCharacters()
        }
        delay(2000)
        loadingEncounters.await()
        state.loading = false
    }
    LazyColumn(modifier = Modifier
        .fillMaxHeight()
        .padding(16.dp)) {
        items(state.characters, key = { it.id!! }) { character ->
            Row(modifier = Modifier.animateItemPlacement()) {
                CharacterListItem(
                    character = character,
                    onEditPressed = {
                        navHostController.navigate("editcharacter?id=${character.id}")
                    },
                    onDeletePressed = {
                        state.toDelete = character
                        state.deleting = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (state.deleting) {
        AlertDialog(
            onDismissRequest = { state.deleting = false },
            title = { Text(text = "Confirm Deletion") },
            text = { Text(text = "Are you sure you wish to delete ${state.toDelete.name}?") },
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
                            viewModel.deleteCharacter()
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


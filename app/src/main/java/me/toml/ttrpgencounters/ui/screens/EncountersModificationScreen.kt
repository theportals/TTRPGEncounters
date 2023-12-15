package me.toml.ttrpgencounters.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.ui.components.CharacterListItem
import me.toml.ttrpgencounters.ui.components.FormField
import me.toml.ttrpgencounters.ui.components.NPCListItem
import me.toml.ttrpgencounters.ui.viewmodels.EncounterModificationViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EncountersModificationScreen(navHostController: NavHostController, id: String?) {
    val viewModel: EncounterModificationViewModel = viewModel()
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.setupInitialState(id)
        viewModel.getCharacters()
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            navHostController.popBackStack()
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        FormField(value = state.title, onValueChange = { state.title = it }, placeholder = { Text(text = "Name") })
        Spacer(modifier = Modifier.height(4.dp))
        FormField(value = state.description, onValueChange = { state.description = it }, placeholder = { Text(text = "Description") })



        Text(text = "Characters")
        LazyColumn(modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(.25f)
        ) {
            items(state.chars, key = { it.id!! }) { character ->
                Row(modifier = Modifier.animateItemPlacement()) {
                    CharacterListItem(character = character, embedded = true)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        TextButton(onClick = { viewModel.openAddCharacterDialogue() }) {
            Text(text = "Edit characters")
        }

        Text(text = "NPCs")
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(.5f)
        ) {
            items(state.npcs, key = { it.id!! }) { npc ->
                Row(modifier = Modifier.animateItemPlacement()) {
                    NPCListItem(
                        npc = npc,
                        onDeletePressed = { viewModel.deleteNPC(npc) },
                        onEditPressed = { viewModel.openNPCDialogue(npc) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = {
                viewModel.openNPCDialogue(null)
            }) {
                Text(text = "Add NPC")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navHostController.popBackStack() }) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                scope.launch {
                    viewModel.saveEncounter()
                }
            }) {
                Text(text = "Save")
            }
        }
        Text(
            text = state.errorMessage,
            style = TextStyle(color = MaterialTheme.colors.error),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        // Load Banner Ad
        // app ID: ca-app-pub-3134219464875667~8769782787
        // test ID: ca-app-pub-3940256099942544/6300978111
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }

    if (state.addCharacter) {
        // Character add dialog
        AlertDialog(
            onDismissRequest = { state.addCharacter = false },
            title = { Text(text = "Edit Characters") },
            text = {
                Text(text = "Select characters to include:")
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    items(state.userCharacters, key = { it.id!! }) { character ->
                        Row (
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            var add by remember { mutableStateOf(viewModel.hasCharacter(character)) }
                            Checkbox(checked = add, onCheckedChange = {
                                add = it
                                if (add) state.charsToAdd.add(character)
                                else state.charsToAdd.remove(character)
                            })
                            Text(text = character.name!!)
                        }
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        state.addCharacter = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.surface)
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addCharacters()
                        state.addCharacter = false
                    }
                ) {
                    Text(text = "Save Characters")
                }
            }
        )
    }

    if (state.addingNPC) {
        // Add NPC Dialogue
        AlertDialog(
            onDismissRequest = { state.addingNPC = false },
            title = { Text(text = "Save NPC") },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormField(value = state.npcName, onValueChange = { state.npcName = it }, placeholder = { Text(text = "Name") })
                    Spacer(modifier = Modifier.height(4.dp))
                    FormField(value = state.npcDescription, onValueChange = { state.npcDescription = it }, placeholder = { Text(text = "Description") })
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.npcInitiative.toString(),
                        onValueChange = { viewModel.updatenpcInitiative(it) },
                        isError = state.npcInitiativeError,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                        label = { Text(text = "Initiative") },
                        maxLines = 1
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.npcHealth.toString(),
                        onValueChange = { viewModel.updatenpcHealth(it) },
                        isError = state.npcHealthError,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                        label = { Text(text = "Health") },
                        maxLines = 1
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.npcArmor.toString(),
                        onValueChange = { viewModel.updatenpcArmor(it) },
                        isError = state.npcArmorError,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                        label = { Text(text = "Armor") },
                        maxLines = 1
                    )
                    Text(
                        text = state.npcErrorMessage,
                        style = TextStyle(color = MaterialTheme.colors.error),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { state.addingNPC = false },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.surface)
                ) {
                   Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveNPC()
                    }
                ) {
                    Text(text = "Save NPC")
                }
            }
        )
    }
}
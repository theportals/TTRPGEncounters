package me.toml.ttrpgencounters.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import me.toml.ttrpgencounters.ui.components.FormField
import me.toml.ttrpgencounters.ui.components.MobListItem
import me.toml.ttrpgencounters.ui.viewmodels.RunEncounterViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunEncounterScreen(navHostController: NavHostController, id: String) {
    val viewModel: RunEncounterViewModel = viewModel()
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.getEncounter(id)
        viewModel.setupMobList()
    }
    if (state.error) Text(
        text = "There was an error retrieving the encounter",
        style = TextStyle(color = MaterialTheme.colors.error)
    )

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { viewModel.openNPCDialogue(null) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add NPC"
                    )
                }
                if (state.rolledInitiative) {
                    Spacer(modifier = Modifier.width(16.dp))
                    FloatingActionButton(onClick = { viewModel.prevTurn() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Turn"
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    FloatingActionButton(onClick = { viewModel.nextTurn() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next turn"
                        )
                    }
                }
            }
        },

        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedVisibility(visible = !state.rolledInitiative) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.rollInitiative()
                    }) {
                    Text(text = "Roll initiative!")
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                items(state.mobs, key = { it.id!! }) { mob ->
                    var selected by remember { mutableStateOf(false) }
                    LaunchedEffect(viewModel.isTurn(mob)) {
                        selected = viewModel.isTurn(mob)
                    }
                    Row(modifier = Modifier.animateItemPlacement()) {
                        MobListItem(
                            mob = mob,
                            selected = selected,
                            onHealthPressed = { viewModel.openHealthDialog(mob) },
                            onTurnDecrementPressed = { viewModel.decreaseTurn(mob) },
                            onTurnIncrementPressed = { viewModel.increaseTurn(mob) },
                            onDeletePressed = { viewModel.delete(mob) },
                            onEditPressed = { viewModel.openNPCDialogue(mob) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (state.changingHealth) {
        AlertDialog(
            onDismissRequest = { state.changingHealth = false },
            title = { Text(text = "Damage Creature") },
            text = {
                Column() {
                    Text(text = "Enter damage (enter negative values to heal)")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.healthDelta.toString(),
                        onValueChange = { viewModel.updateHealthDelta(it) },
                        isError = state.healthError,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                        label = { Text(text = "Damage") },
                        maxLines = 1
                    )
                    Text(
                        text = state.healthErrorMessage,
                        style = TextStyle(color = MaterialTheme.colors.error),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                }

            },
            dismissButton = {
                Button(
                    onClick = { state.changingHealth = false },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.surface)
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveHealth()
                    }
                ) {
                    Text(text = "Damage Creature")
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
                    FormField(
                        value = state.npcName,
                        onValueChange = { state.npcName = it },
                        placeholder = { Text(text = "Name") })
                    Spacer(modifier = Modifier.height(4.dp))
                    FormField(
                        value = state.npcDescription,
                        onValueChange = { state.npcDescription = it },
                        placeholder = { Text(text = "Description") })
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
                        viewModel.saveMob()
                    }
                ) {
                    Text(text = "Save NPC")
                }
            }
        )
    }


    if (state.changingHealth) {
        AlertDialog(
            onDismissRequest = { state.changingHealth = false },
            title = { Text(text = "Damage Creature") },
            text = {
                Column() {
                    Text(text = "Enter damage (enter negative values to heal)")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.healthDelta.toString(),
                        onValueChange = { viewModel.updateHealthDelta(it) },
                        isError = state.healthError,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                        label = { Text(text = "Damage") },
                        maxLines = 1
                    )
                    Text(
                        text = state.healthErrorMessage,
                        style = TextStyle(color = MaterialTheme.colors.error),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                }

            },
            dismissButton = {
                Button(
                    onClick = { state.changingHealth = false },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.surface)
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveHealth()
                    }
                ) {
                    Text(text = "Damage Creature")
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
                    FormField(
                        value = state.npcName,
                        onValueChange = { state.npcName = it },
                        placeholder = { Text(text = "Name") })
                    Spacer(modifier = Modifier.height(4.dp))
                    FormField(
                        value = state.npcDescription,
                        onValueChange = { state.npcDescription = it },
                        placeholder = { Text(text = "Description") })
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
                        viewModel.saveMob()
                    }
                ) {
                    Text(text = "Save NPC")
                }
            }
        )
    }
}


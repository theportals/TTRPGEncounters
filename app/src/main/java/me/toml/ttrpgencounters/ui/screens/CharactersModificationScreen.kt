package me.toml.ttrpgencounters.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.ui.components.FormField
import me.toml.ttrpgencounters.ui.viewmodels.CharacterModificationViewModel

@Composable
fun CharactersModificationScreen(navHostController: NavHostController, id: String?) {
    val viewModel: CharacterModificationViewModel = viewModel()
    val state = viewModel.uiState
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.setupInitialState(id)
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            navHostController.popBackStack()
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        FormField(value = state.name, onValueChange = { state.name = it }, placeholder = { Text(text = "Name") })
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.3f),
                value = state.age.toString(),
                onValueChange = { viewModel.updateAge(it) },
                isError = state.ageError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                label = { Text(text = "Age") },
                trailingIcon = { Text(text = "years") },
                maxLines = 1
            )
            Spacer(modifier= Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.5f),
                value = state.initiative.toString(),
                onValueChange = { viewModel.updateInitiative(it) },
                isError = state.initiativeError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                label = { Text(text = "Initiative") },
                maxLines = 1
            )
            Spacer(modifier= Modifier.width(8.dp))
            OutlinedTextField(
                value = state.race,
                onValueChange = { state.race = it },
                label = { Text(text = "Race") },
                maxLines = 1
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.333f),
                value = state.height.toString(),
                onValueChange = { viewModel.updateHeight(it) },
                isError = state.heightError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                label = { Text(text = "Height") },
                trailingIcon = { Text(text = "cm") },
                maxLines = 1
            )
            Spacer(modifier= Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.5f),
                value = state.job,
                onValueChange = { state.job = it },
                label = { Text(text = "Class") },
                maxLines = 1
            )
            Spacer(modifier= Modifier.width(8.dp))
            OutlinedTextField(
                value = state.gender,
                onValueChange = { state.gender = it },
                label = { Text(text = "Gender") },
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.5f),
                value = state.health.toString(),
                onValueChange = { viewModel.updateHealth(it) },
                isError = state.healthError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                label = { Text(text = "Health") },
                trailingIcon = { Text(text = "hp") },
                maxLines = 1
            )
            Spacer(modifier= Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.armor.toString(),
                onValueChange = { viewModel.updateArmor(it) },
                isError = state.armorError,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                label = { Text(text = "Armor") },
                trailingIcon = { Text(text = "ac") },
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        FormField(value = state.description, onValueChange = { state.description = it }, placeholder = { Text(text = "Description") })
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navHostController.popBackStack() }) {
                Text(text = "Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                scope.launch {
                    viewModel.saveCharacter()
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
    }
}


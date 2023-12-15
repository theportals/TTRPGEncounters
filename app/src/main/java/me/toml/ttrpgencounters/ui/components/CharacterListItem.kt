package me.toml.ttrpgencounters.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.ui.models.Toon
import me.toml.ttrpgencounters.ui.theme.TTRPGEncountersTheme

enum class CharSwipeState {
    OPEN,
    CLOSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharacterListItem(
    character: Toon,
    onDeletePressed: () -> Unit = {},
    onEditPressed: () -> Unit = {},
    embedded: Boolean = false
) {
    var showDetails by remember { mutableStateOf(false) }
    val swipeableState = rememberSwipeableState(initialValue = CharSwipeState.CLOSED)
    val anchors = mapOf(
        0f to CharSwipeState.CLOSED,
        -200f to CharSwipeState.OPEN
    )
    val scope = rememberCoroutineScope()
    var mod = Modifier.fillMaxWidth()
    if (!embedded) mod = mod.swipeable(
        state = swipeableState,
        anchors = anchors,
        orientation = Orientation.Horizontal
    )
    Box(
        modifier = mod
    ) {
        if (!embedded) {
            Row(
                modifier = Modifier
                    .height(101.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onDeletePressed()
                        scope.launch {
                            swipeableState.animateTo(CharSwipeState.CLOSED)
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.5f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
        Surface(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.toInt(), 0) }
                .clickable { showDetails = !showDetails },
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.padding(0.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = character.name ?: "",
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                    if (!embedded) {
                        IconButton(onClick = onEditPressed) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit button"
                            )
                        }
                    }
                }
                Row(modifier = Modifier.padding(16.dp, 0.dp)) {
                    Text(text = (character.race ?: "") + " " + (character.job ?: ""))
                }
                AnimatedVisibility(visible = showDetails) {
                    Column() {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.padding(16.dp, 0.dp),

                        ) {
                            val initiative = character.initiative ?: 0
                            Text(text = "${if (initiative >= 0) "+" else ""}${initiative} initiative")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "${character.maxHealth ?: 0} health")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "${character.armor ?: 0} armor")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = character.description ?: "")
                            if (character.age != 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${character.age} years old")
                            }
                            if (character.height != 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${character.height} cm tall")
                            }
                            if (character.gender != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = character.gender)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CharacterItemPreview() {
    TTRPGEncountersTheme() {
        CharacterListItem(character = Toon(
            id = "fj9810asdf0a",
            userId = "901mas9df0a",
            name = "Frindle Babbin",
            age = 30,
            race = "Dwarf",
            job = "Warrior",
            height = 125,
            gender = "Male",
            description = "A mighty warrior",
        ))
    }
}
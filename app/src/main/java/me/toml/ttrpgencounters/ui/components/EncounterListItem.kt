package me.toml.ttrpgencounters.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.toml.ttrpgencounters.R
import me.toml.ttrpgencounters.ui.models.Encounter
import me.toml.ttrpgencounters.ui.theme.TTRPGEncountersTheme

enum class SwipeState {
    OPEN,
    CLOSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EncounterListItem(
    encounter: Encounter,
    onRunPressed: () -> Unit = {},
    onDeletePressed: () -> Unit = {},
    onEditPressed: () -> Unit = {}
) {
    var showDetails by remember { mutableStateOf(false) }
    var swipeableState = rememberSwipeableState(initialValue = SwipeState.CLOSED)
    val scope = rememberCoroutineScope()
    val anchors = mapOf(
        0f to SwipeState.CLOSED,
        -200f to SwipeState.OPEN
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                orientation = Orientation.Horizontal
            )
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    onDeletePressed()
                    scope.launch { swipeableState.animateTo(SwipeState.CLOSED) }
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
        Surface(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.toInt(), 0) }
                .clickable {
                    showDetails = !showDetails
                },
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = encounter.title ?: "",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(14.dp)
                    )
                    IconButton(onClick = onRunPressed) {
                        Icon(painterResource(R.drawable.ic_baseline_play_arrow_24), contentDescription = "Run encounter")
                    }
                }

                AnimatedVisibility(visible = showDetails) {
                    Column {
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.padding(16.dp, 0.dp)) {
                            Text(text = encounter.description ?: "")
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier.padding(16.dp, 0.dp)) {
                            if (encounter.chars.isNotEmpty()) {
                                Text(text = "Characters:")
                                Column(modifier = Modifier.padding(16.dp, 0.dp)) {
                                    for (i in 0..2) {
                                        val char = encounter.chars.getOrNull(i)
                                        if (char != null) {
                                            Text(text = char.name!!)
                                        }
                                    }
                                    if (encounter.chars.size > 3) Text(text = "+${encounter.chars.size - 3} more")

                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (encounter.npcs.isNotEmpty()) {
                                Text(text = "NPCs:")
                                Column(modifier = Modifier.padding(16.dp, 0.dp)) {
                                    for (i in 0..2) {
                                        val npc = encounter.npcs.getOrNull(i)
                                        if (npc != null) {
                                            Text(text = npc.name!!)
                                        }
                                    }
                                    if (encounter.npcs.size > 3) Text(text = "+${encounter.npcs.size - 3} more")
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Button(onClick = onEditPressed) {
                                Text(text = "Edit Encounter")
                            }
                            Button(
                                onClick = onRunPressed,
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
                            ) {
                                Text(text = "Run Encounter")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EncounterListItemPreview() {
    TTRPGEncountersTheme {
        EncounterListItem(encounter = Encounter(
            title = "Test Encounter",
            description = "This is a test encounter. It will surely test the players."
        ))
    }
}
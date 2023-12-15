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
import me.toml.ttrpgencounters.ui.models.NPC

enum class NPCSwipeState {
    OPEN,
    CLOSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NPCListItem(
    npc: NPC,
    onDeletePressed: () -> Unit = {},
    onEditPressed: () -> Unit = {}
) {
    var showDetails by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeableState(initialValue = NPCSwipeState.CLOSED)
    val anchors = mapOf(
        0f to NPCSwipeState.CLOSED,
        -200f to NPCSwipeState.OPEN
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeState,
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
                .offset { IntOffset(swipeState.offset.value.toInt(), 0) }
                .clickable {
                    showDetails = !showDetails
                },
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = npc.name ?: "",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(16.dp)
                    )
                    IconButton(onClick = onEditPressed) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit NPC")
                    }
                }

                AnimatedVisibility(visible = showDetails) {
                    Column {
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.padding(16.dp, 0.dp)) {
                            Column {
                                val initiative = npc.initiative ?: 0
                                Text(text = "${if (initiative >= 0) "+" else ""}${initiative} initiative")
                                Text(text = "${npc.maxHealth ?: 0} health")
                                Text(text = "${npc.armor ?: 0} armor")
                                Text(text = npc.description ?: "")
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
fun NPCListItemPreview() {
    NPCListItem(npc = NPC(
        name = "Test creature",
        description = "A fierce test creature",
        initiative = 0
    ))
}

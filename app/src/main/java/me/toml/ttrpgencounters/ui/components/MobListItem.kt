package me.toml.ttrpgencounters.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import me.toml.ttrpgencounters.ui.models.EncounterMob
import me.toml.ttrpgencounters.ui.models.NPC
import me.toml.ttrpgencounters.ui.models.Toon

enum class MobSwipeState {
    OPEN,
    CLOSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MobListItem(
    mob: EncounterMob,
    onDeletePressed: () -> Unit = {},
    onEditPressed: () -> Unit = {},
    onHealthPressed: () -> Unit = {},
    onTurnIncrementPressed: () -> Unit = {},
    onTurnDecrementPressed: () -> Unit = {},
    selected: Boolean
) {
    var showDetails by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeableState(initialValue = MobSwipeState.CLOSED)
    val anchors = mapOf(
        0f to MobSwipeState.CLOSED,
        -200f to MobSwipeState.OPEN
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
                onClick = onDeletePressed,
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .fillMaxHeight(),
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
            shape = RoundedCornerShape(2.dp),
            border = if (selected) BorderStroke(2.dp, MaterialTheme.colors.secondary) else null
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = mob.name ?: "",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "${mob.armor ?: "0"} AC",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "${mob.health}/${mob.maxHealth ?: "0"} HP",
                        style = MaterialTheme.typography.subtitle2.copy(color = (if (mob.health < (mob.maxHealth ?: 0)) Color.Red else Color.Black)),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { onHealthPressed() },
                    )
                    if (mob is NPC) {
                        IconButton(onClick = onEditPressed) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Mob")
                        }
                    } else {
                        Spacer(modifier = Modifier.fillMaxHeight().width(64.dp))
                    }
                }

                AnimatedVisibility(visible = showDetails) {
                    Column {
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.padding(16.dp, 0.dp)) {
                            Column {
                                Row() {
                                    Text(text = "${mob.health}/${mob.maxHealth ?: 0} health")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "${mob.armor ?: 0} armor")
                                }
                                val initiative = mob.initiative ?: 0
                                Text(text = "${if (initiative >= 0) "+" else ""}${initiative} initiative")

                                Text(text = mob.description ?: "")
                            }
                        }

                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(.66f)) {
                                Button(
                                    onClick = onTurnDecrementPressed,
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Move up in turn order"
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = onTurnIncrementPressed
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Move down in turn order"
                                    )
                                }
                            }
                            Row() {
                                Button(onClick = onHealthPressed) {
                                    Text(text = "Damage")
                                }
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
fun MobListItemPreview() {
    MobListItem(mob = EncounterMob(
        name = "Test mob",
        description = "Evil test mob",
        maxHealth = 30,
        health = 15,
        armor = 12
    ), selected = false)
}
package me.toml.ttrpgencounters.ui.models

data class Encounter(
    val id: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val npcs: List<NPC> = emptyList(),
    val chars: ArrayList<Toon> = ArrayList()
)
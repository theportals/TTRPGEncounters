package me.toml.ttrpgencounters.ui.models

data class NPC(
    override val name: String? = null,
    override val description: String? = null,
    override val initiative: Int? = null,
    override val id: String? = null,
    override val maxHealth: Int? = null,
    override val armor: Int? = null,
    override var health: Int = 0
): EncounterMob()
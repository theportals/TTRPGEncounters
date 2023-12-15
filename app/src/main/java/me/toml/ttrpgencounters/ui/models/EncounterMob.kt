package me.toml.ttrpgencounters.ui.models

open class EncounterMob(
    open val name: String? = null,
    open val description: String? = null,
    open val initiative: Int? = 0,
    open val id: String? = null,
    open val maxHealth: Int? = 0,
    open val armor: Int? = 0,
    open var health: Int = maxHealth ?: 0,
    var initiativeRolled: Int = 0
)
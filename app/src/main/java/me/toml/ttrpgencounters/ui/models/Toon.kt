package me.toml.ttrpgencounters.ui.models

data class Toon(
    override val id: String? = null,
    val userId: String? = null,
    override val name: String? = null,
    val age: Int? = null,
    val race: String? = null,
    val job: String? = null,    // Kotlin gets mad at the "class" keyword, so I used job instead
    val height: Int? = null,
    val gender: String? = null,
    override val description: String? = null,
    override val initiative: Int? = null,
    override val maxHealth: Int? = null,
    override val armor: Int? = null
): EncounterMob()
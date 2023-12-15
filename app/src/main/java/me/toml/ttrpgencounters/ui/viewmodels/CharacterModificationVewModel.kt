package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.repositories.CharactersRepository


class CharacterModificationScreenState {
    var name by mutableStateOf("")
    var age by mutableStateOf(0)
    var race by mutableStateOf("")
    var job by mutableStateOf("")
    var height by mutableStateOf(0)
    var gender by mutableStateOf("")
    var description by mutableStateOf("")
    var initiative by mutableStateOf(0)
    var health by mutableStateOf(0)
    var armor by mutableStateOf(0)

    var errorMessage by mutableStateOf("")
    var ageError by mutableStateOf(false)
    var heightError by mutableStateOf(false)
    var nameError by mutableStateOf(false)
    var saveSuccess by mutableStateOf(false)
    var initiativeError by mutableStateOf(false)
    var healthError by mutableStateOf(false)
    var armorError by mutableStateOf(false)
}

class CharacterModificationViewModel(application: Application): AndroidViewModel(application) {
    val uiState = CharacterModificationScreenState()
    var id: String? = null

    suspend fun setupInitialState(id: String?) {
        if (id == null || id == "new") return
        this.id = id
        val character = CharactersRepository.getCharacters().find { it.id == id } ?: return
        uiState.name = character.name ?: ""
        uiState.age = character.age ?: 0
        uiState.race = character.race ?: ""
        uiState.job = character.job ?: ""
        uiState.height = character.height ?: 0
        uiState.gender = character.gender ?: ""
        uiState.description = character.description ?: ""
        uiState.initiative = character.initiative ?: 0
        uiState.health = character.maxHealth ?: 0
        uiState.armor = character.armor ?: 0
    }

    fun updateAge(input: String) {
        uiState.ageError = false
        uiState.errorMessage = ""
        try {
            uiState.age = input.filter { !it.isWhitespace() }.toInt()
        } catch (e: Exception) {
            uiState.ageError = true
            uiState.errorMessage = "Age must be a whole number greater than 0"
        }
    }

    fun updateInitiative(input: String) {
        uiState.initiativeError = false
        uiState.errorMessage = ""
        try {
            uiState.initiative = input.filter { !it.isWhitespace() }.toInt()
        } catch (e: Exception) {
            uiState.initiativeError = true
            uiState.errorMessage = "Initiative must be a whole number"
        }
    }

    fun updateHeight(input: String) {
        uiState.heightError = false
        uiState.errorMessage = ""
        try {
            uiState.height = input.filter {!it.isWhitespace()}.toInt()
        } catch (e: Exception) {
            uiState.heightError = true
            uiState.errorMessage = "Height must be a whole number greater than 0"
        }
    }

    fun updateHealth(input: String) {
        uiState.healthError = false
        uiState.errorMessage = ""
        try {
            uiState.health = input.filter {!it.isWhitespace()}.toInt()
        } catch (e: Exception) {
            uiState.healthError = true
            uiState.errorMessage = "Health must be a whole number greater than 0"
        }
    }

    fun updateArmor(input: String) {
        uiState.armorError = false
        uiState.errorMessage = ""
        try {
            uiState.armor = input.filter {!it.isWhitespace()}.toInt()
        } catch (e: Exception) {
            uiState.armorError = true
            uiState.errorMessage = "Armor must be a whole number greater than 0"
        }
    }

    suspend fun saveCharacter() {
        if (uiState.ageError || uiState.heightError) return

        uiState.errorMessage = ""
        uiState.nameError = false

        if (uiState.name.isEmpty()) {
            uiState.nameError = true
            uiState.errorMessage = "Name cannot be blank"
            return
        }

        if (uiState.health <= 0) {
            uiState.healthError = true
            uiState.errorMessage = "Health must be greater than 0"
            return
        }

        if (uiState.armor <= 0) {
            uiState.armorError = true
            uiState.errorMessage = "Armor must be greater than 0"
            return
        }

        if (id == null) {
            // Create new character
            CharactersRepository.createCharacter(
                uiState.name,
                uiState.age,
                uiState.race,
                uiState.job,
                uiState.height,
                uiState.gender,
                uiState.description,
                uiState.initiative,
                uiState.health,
                uiState.armor)
        } else {
            // Update character
            val character = CharactersRepository.getCharacters().find {it.id == id} ?: return
            CharactersRepository.updateCharacter(
                character.copy(
                    name = uiState.name,
                    age = uiState.age,
                    race = uiState.race,
                    job = uiState.job,
                    height = uiState.height,
                    gender = uiState.gender,
                    description = uiState.description,
                    initiative = uiState.initiative,
                    maxHealth = uiState.health,
                    armor = uiState.armor
                )
            )
        }
        uiState.saveSuccess = true
    }
}
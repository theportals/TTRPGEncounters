package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.models.NPC
import me.toml.ttrpgencounters.ui.models.Toon
import me.toml.ttrpgencounters.ui.repositories.CharactersRepository
import me.toml.ttrpgencounters.ui.repositories.EncountersRepository

class EncounterModificationScreenState {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    val _npcs = mutableStateListOf<NPC>()
    val npcs: List<NPC> get() = _npcs
    val _chars = mutableStateListOf<Toon>()
    val chars: ArrayList<Toon> get() = ArrayList(_chars)

    var addingNPC by mutableStateOf(false)
    var addCharacter by mutableStateOf(false)

    var npcName by mutableStateOf("")
    var npcDescription by mutableStateOf("")
    var npcInitiative by mutableStateOf(0)
    var npcNameError by mutableStateOf(false)
    var npcInitiativeError by mutableStateOf(false)
    var npcErrorMessage by mutableStateOf("")
    var npcToEdit by mutableStateOf(NPC())
    var npcHealth by mutableStateOf(0)
    var npcArmor by mutableStateOf(0)
    var npcHealthError by mutableStateOf(false)
    var npcArmorError by mutableStateOf(false)

    val _userCharacters = mutableStateListOf<Toon>()
    val userCharacters: List<Toon> get() = _userCharacters
    val charsToAdd = mutableStateListOf<Toon>()

    var errorMessage by mutableStateOf("")
    var titleError by mutableStateOf(false)
    var saveSuccess by mutableStateOf(false)

    var counter by mutableStateOf(0) // Used to give each NPC a unique ID
}

class EncounterModificationViewModel(application: Application): AndroidViewModel(application) {
    val uiState = EncounterModificationScreenState()
    var id: String? = null

    suspend fun setupInitialState(id: String?) {
        if (id == null || id == "new") return
        this.id = id
        val encounter = EncountersRepository.getEncounters().find { it.id == id } ?: return
        uiState.title = encounter.title ?: ""
        uiState.description = encounter.description ?: ""
        uiState._npcs.clear()
        uiState._npcs.addAll(encounter.npcs)
        uiState._chars.clear()
        uiState._chars.addAll(encounter.chars)
        uiState.counter = uiState._npcs.size
    }

    suspend fun saveEncounter() {
        // if (errors) return

        uiState.errorMessage = ""
        uiState.titleError = false

        if (uiState.title.isEmpty()) {
            uiState.titleError = true
            uiState.errorMessage = "Title cannot be blank"
            return
        }
        if (id == null) {
            // Make a new encounter
            EncountersRepository.createEncounter(
                uiState.title,
                uiState.description,
                uiState.npcs,
                uiState.chars
            )
        } else {
            // Update an existing encounter
            val encounter = EncountersRepository.getEncounters().find { it.id == id } ?: return
            EncountersRepository.updateEncounter(
                encounter.copy(
                    title = uiState.title,
                    description = uiState.description,
                    npcs = uiState.npcs,
                    chars = uiState.chars
                )
            )
        }
        uiState.saveSuccess = true
    }

    fun openNPCDialogue(npc: NPC?) {
        if (npc != null) {
            uiState.npcName = npc.name ?: ""
            uiState.npcDescription = npc.description ?: ""
            uiState.npcInitiative = npc.initiative ?: 0
            uiState.npcHealth = npc.maxHealth ?: 0
            uiState.npcArmor = npc.armor ?: 0
            uiState.npcToEdit = npc
        }
        uiState.addingNPC = true
    }

    fun saveNPC() {
        if (uiState.npcInitiativeError) return

        uiState.npcErrorMessage = ""
        uiState.npcNameError = false

        if (uiState.npcName.isEmpty()) {
            uiState.npcNameError = true
            uiState.npcErrorMessage = "Name cannot be blank"
            return
        }

        if (uiState.npcHealth < 1) {
            uiState.npcHealthError = true
            uiState.npcErrorMessage = "Health must be greater than 0"
            return
        }

        if (uiState.npcArmor < 1) {
            uiState.npcArmorError = true
            uiState.npcErrorMessage = "Armor must be greater than 0"
            return
        }

        if (uiState.npcToEdit.id == null) {
            // Add a new NPC
            uiState._npcs.add(
                NPC(
                    name = uiState.npcName,
                    description = uiState.npcDescription,
                    initiative = uiState.npcInitiative,
                    id = uiState.npcName + uiState.counter,
                    maxHealth = uiState.npcHealth,
                    armor = uiState.npcArmor,
                )
            )
            uiState.counter += 1
        } else {
            val npc = uiState._npcs.find { it.id == uiState.npcToEdit.id } ?: return
            uiState._npcs.remove(npc)
            uiState._npcs.add(NPC(
                name = uiState.npcName,
                description = uiState.npcDescription,
                initiative = uiState.npcInitiative,
                id = npc.id,
                maxHealth = uiState.npcHealth,
                armor = uiState.npcArmor
            ))
            uiState.npcToEdit = NPC()
        }

        uiState.addingNPC = false
    }

    fun updatenpcInitiative(input: String) {
        uiState.npcInitiativeError = false
        uiState.errorMessage = ""
        try {
            uiState.npcInitiative = input.filter { !it.isWhitespace() }.toInt()
        } catch (e: Exception) {
            uiState.npcInitiativeError = true
            uiState.npcErrorMessage = "Initiative must be a whole number"
        }
    }

    fun updatenpcHealth(input: String) {
        uiState.npcHealthError = false
        uiState.errorMessage = ""
        try {
            uiState.npcHealth = input.filter { !it.isWhitespace() }.toInt()
            if (uiState.npcHealth < 1) {
                uiState.npcHealthError = true
                uiState.npcErrorMessage = "Health must be a whole number greater than 0"
            }
        } catch (e: Exception) {
            uiState.npcHealthError = true
            uiState.npcErrorMessage = "Health must be a whole number greater than 0"
        }
    }

    fun updatenpcArmor(input: String) {
        uiState.npcArmorError = false
        uiState.errorMessage = ""
        try {
            uiState.npcArmor = input.filter { !it.isWhitespace() }.toInt()
            if (uiState.npcArmor < 1) {
                uiState.npcArmorError = true
                uiState.npcErrorMessage = "Armor must be a whole number greater than 0"
            }
        } catch (e: Exception) {
            uiState.npcArmorError = true
            uiState.npcErrorMessage = "Armor must be a whole number greater than 0"
        }
    }

    fun openAddCharacterDialogue() {
        uiState.charsToAdd.clear()
        uiState.charsToAdd.addAll(uiState._chars)
        uiState.addCharacter = true
    }

    fun addCharacters() {
        uiState._chars.clear()
        uiState._chars.addAll(uiState.charsToAdd)
    }

    fun hasCharacter(character: Toon): Boolean {
        return uiState._chars.contains(character)
    }

    suspend fun getCharacters() {
        val characters = CharactersRepository.getCharacters()
        uiState._userCharacters.clear()
        uiState._userCharacters.addAll(characters)
    }

    fun deleteNPC(npc: NPC) {
        uiState._npcs.remove(npc)
    }
}
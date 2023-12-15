package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.models.Encounter
import me.toml.ttrpgencounters.ui.models.EncounterMob
import me.toml.ttrpgencounters.ui.models.NPC
import me.toml.ttrpgencounters.ui.repositories.EncountersRepository
import kotlin.random.Random

class RunEncounterScreenState {
    var encounter by mutableStateOf(Encounter())
    var error by mutableStateOf(false)
    val _mobs = mutableStateListOf<EncounterMob>()
    val mobs: List<EncounterMob> get() = _mobs
    var rolledInitiative by mutableStateOf(false)

    var upNext by mutableStateOf(EncounterMob())

    var changingHealth by mutableStateOf(false)
    var healthDelta by mutableStateOf(0)
    var health by mutableStateOf(0)
    var healthError by mutableStateOf(false)
    var healthErrorMessage by mutableStateOf("")
    var mobToDamage by mutableStateOf(EncounterMob())

    var addingNPC by mutableStateOf(false)
    var npcName by mutableStateOf("")
    var npcDescription by mutableStateOf("")
    var npcInitiative by mutableStateOf(0)
    var npcNameError by mutableStateOf(false)
    var npcInitiativeError by mutableStateOf(false)
    var npcErrorMessage by mutableStateOf("")
    var npcToEdit by mutableStateOf(EncounterMob())
    var npcHealth by mutableStateOf(0)
    var npcArmor by mutableStateOf(0)
    var npcHealthError by mutableStateOf(false)
    var npcArmorError by mutableStateOf(false)

    var counter by mutableStateOf(0) // Used to give each NPC a unique ID

    var errorMessage by mutableStateOf("")
}

class RunEncounterViewModel(application: Application): AndroidViewModel(application) {
    val uiState = RunEncounterScreenState()

    suspend fun getEncounter(id: String) {
        val encounter = EncountersRepository.getEncounter(id)
        if (encounter == null) {
            uiState.error = true
            return
        }
        uiState.encounter = encounter
    }

    fun setupMobList() {
        uiState._mobs.addAll(uiState.encounter.chars)
        uiState._mobs.addAll(uiState.encounter.npcs)
        uiState._mobs.forEach { mob -> mob.health = mob.maxHealth ?: 0 }
        uiState.counter = uiState._mobs.size
    }

    fun rollInitiative() {
        uiState.rolledInitiative = true
        if (uiState.mobs.isEmpty()) return
        uiState.mobs.forEach { mob ->
            var roll = Random.nextInt(20) + 1
            roll += mob.initiative ?: 0
            mob.initiativeRolled = roll
        }
        // Sort by initiative modifier, then initiative rolled. This should break initiative ties
        uiState._mobs.sortBy { it.initiative }
        uiState._mobs.reverse()
        uiState._mobs.sortBy { it.initiativeRolled }
        uiState._mobs.reverse()
        uiState.upNext = uiState.mobs[0]
    }

    fun nextTurn() {
        if (uiState.mobs.isEmpty()) return
        var index = uiState.mobs.indexOf(uiState.upNext) + 1
        if (index >= uiState.mobs.size) index = 0
        uiState.upNext = uiState.mobs[index]
    }

    fun prevTurn() {
        if (uiState.mobs.isEmpty()) return
        var index = uiState.mobs.indexOf(uiState.upNext) - 1
        if (index < 0) index = uiState.mobs.size - 1
        uiState.upNext = uiState.mobs[index]
    }

    fun isTurn(mob: EncounterMob): Boolean {
        return uiState.upNext == mob
    }

    fun openHealthDialog(mob: EncounterMob) {
        uiState.mobToDamage = mob
        uiState.healthDelta = 0
        uiState.changingHealth = true
    }

    fun saveHealth() {
        uiState.mobToDamage.health -= uiState.healthDelta
        uiState.changingHealth = false
    }

    fun updateHealthDelta(input: String) {
        uiState.healthError = false
        uiState.healthErrorMessage = ""
        try {
            uiState.healthDelta = input.filter { !it.isWhitespace() }.toInt()
        } catch (e: Exception) {
            uiState.healthError = true
            uiState.healthErrorMessage = "Change in health must be a whole number"
        }
    }

    fun decreaseTurn(mob: EncounterMob) {
        val oldIndex = uiState.mobs.indexOf(mob)
        if (oldIndex == 0) return
        val newIndex = oldIndex - 1
        val victim = uiState.mobs[newIndex]
        uiState._mobs[newIndex] = mob
        uiState._mobs[oldIndex] = victim
    }

    fun increaseTurn(mob: EncounterMob) {
        val oldIndex = uiState.mobs.indexOf(mob)
        if (oldIndex == uiState.mobs.size - 1) return
        val newIndex = oldIndex + 1
        val victim = uiState.mobs[newIndex]
        uiState._mobs[newIndex] = mob
        uiState._mobs[oldIndex] = victim
    }

    fun delete(mob: EncounterMob) {
        uiState._mobs.remove(mob)
    }

    fun saveMob() {
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
            val newNPC = NPC(
                name = uiState.npcName,
                description = uiState.npcDescription,
                initiative = uiState.npcInitiative,
                id = uiState.npcName + uiState.counter,
                maxHealth = uiState.npcHealth,
                armor = uiState.npcArmor,
                health = uiState.npcHealth
            )
            // Add a new NPC
            var roll = Random.nextInt(20) + 1
            roll += newNPC.initiative ?: 0
            newNPC.initiativeRolled = roll
            uiState._mobs.add(newNPC)
            uiState.counter += 1
        } else {
            val npc = uiState._mobs.find { it.id == uiState.npcToEdit.id } ?: return
            uiState._mobs.remove(npc)
            val newNPC = NPC(
                name = uiState.npcName,
                description = uiState.npcDescription,
                initiative = uiState.npcInitiative,
                id = npc.id,
                maxHealth = uiState.npcHealth,
                armor = uiState.npcArmor,
                health = uiState.health,
            )
            newNPC.initiativeRolled = npc.initiativeRolled
            uiState._mobs.add(newNPC)
            uiState.npcToEdit = NPC()
        }

        // Sort by initiative modifier, then initiative rolled. This should break initiative ties
        uiState._mobs.sortBy { it.initiative }
        uiState._mobs.reverse()
        uiState._mobs.sortBy { it.initiativeRolled }
        uiState._mobs.reverse()

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

    fun openNPCDialogue(mob: EncounterMob?) {
        if (mob != null) {
            uiState.npcName = mob.name ?: ""
            uiState.npcDescription = mob.description ?: ""
            uiState.npcInitiative = mob.initiative ?: 0
            uiState.npcHealth = mob.maxHealth ?: 0
            uiState.npcArmor = mob.armor ?: 0
            uiState.npcToEdit = mob
            uiState.health = mob.health
        }
        uiState.addingNPC = true
    }

}
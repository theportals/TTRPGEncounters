package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.models.Encounter
import me.toml.ttrpgencounters.ui.models.Toon
import me.toml.ttrpgencounters.ui.repositories.EncountersRepository

class EncountersScreenState {
    val _encounters = mutableStateListOf<Encounter>()
    val encounters: List<Encounter> get() = _encounters
    var deleting by mutableStateOf(false)
    var toDelete by mutableStateOf(Encounter())
    var loading by mutableStateOf(!EncountersRepository.isInitialized())
}

class EncountersViewModel(application: Application): AndroidViewModel(application) {
    val uiState = EncountersScreenState()

    suspend fun getEncounters() {
        val encounters = EncountersRepository.getEncounters()
        uiState._encounters.clear()
        uiState._encounters.addAll(encounters)
    }

    suspend fun deleteEncounter() {
        uiState._encounters.remove(uiState.toDelete)
        EncountersRepository.deleteEncounter(uiState.toDelete)
    }
}
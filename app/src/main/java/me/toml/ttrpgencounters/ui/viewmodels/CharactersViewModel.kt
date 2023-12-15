package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.models.Toon
import me.toml.ttrpgencounters.ui.repositories.CharactersRepository


class CharactersScreenState {
    val _characters = mutableStateListOf<Toon>()
    val characters: List<Toon> get() = _characters
    var deleting by mutableStateOf(false)
    var toDelete by mutableStateOf(Toon())
    var loading by mutableStateOf(!CharactersRepository.isInitialized())
}

class CharactersViewModel(application: Application): AndroidViewModel(application) {
    val uiState = CharactersScreenState()

    suspend fun getCharacters() {
        val characters = CharactersRepository.getCharacters()
        uiState._characters.clear()
        uiState._characters.addAll(characters)
    }

    suspend fun deleteCharacter() {
        uiState._characters.remove(uiState.toDelete)
        CharactersRepository.deleteCharacter(uiState.toDelete)
    }
}
package me.toml.ttrpgencounters.ui.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import me.toml.ttrpgencounters.ui.models.Toon

object CharactersRepository {

    private val charactersCache = mutableListOf<Toon>()
    private var cacheInitialized = false

    suspend fun getCharacters(): List<Toon> {
        if (!cacheInitialized) {
            val snapshot = Firebase.firestore
                .collection("characters")
                .whereEqualTo("userId", UserRepository.getCurrentUserId())
                .get()
                .await()
            charactersCache.addAll(snapshot.toObjects())
            cacheInitialized = true
        }
        return charactersCache
    }

    suspend fun createCharacter(
        name: String,
        age: Int,
        race: String,
        job: String,
        height: Int,
        gender: String,
        description: String,
        initiative: Int,
        maxHealth: Int,
        armor: Int
    ): Toon {
        val doc = Firebase.firestore.collection(("characters")).document()
        val character = Toon(
            name = name,
            age = age,
            race = race,
            job = job,
            height = height,
            gender = gender,
            description = description,
            id = doc.id,
            userId = UserRepository.getCurrentUserId(),
            initiative = initiative,
            maxHealth = maxHealth,
            armor = armor
        )
        doc.set(character).await()
        charactersCache.add(character)
        return character
    }

    suspend fun updateCharacter(character: Toon) {
        Firebase.firestore
            .collection("characters")
            .document(character.id!!)
            .set(character)
            .await()

        val oldCharacterIndex = charactersCache.indexOfFirst { it.id == character.id }
        charactersCache[oldCharacterIndex] = character
    }

    suspend fun deleteCharacter(character: Toon) {
        Firebase.firestore
            .collection("characters")
            .document(character.id!!)
            .delete()
            .await()

        charactersCache.remove(character)

        // Remove the character from any encounter it's in
        val encounters = EncountersRepository.getEncounters()
        encounters.forEach { encounter ->
            if (encounter.chars.contains(character)) encounter.chars.remove(character)
            EncountersRepository.updateEncounter(encounter)
        }
    }

    fun isInitialized(): Boolean {
        return cacheInitialized
    }
}
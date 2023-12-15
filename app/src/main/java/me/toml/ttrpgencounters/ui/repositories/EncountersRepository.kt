package me.toml.ttrpgencounters.ui.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import me.toml.ttrpgencounters.ui.models.Encounter
import me.toml.ttrpgencounters.ui.models.NPC
import me.toml.ttrpgencounters.ui.models.Toon

object EncountersRepository {

    private val encountersCache = mutableListOf<Encounter>()
    private var cacheInitialized = false

    suspend fun getEncounters(): List<Encounter> {
        if (!cacheInitialized) {
            val snapshot = Firebase.firestore
                .collection("encounters")
                .whereEqualTo("userId", UserRepository.getCurrentUserId())
                .get()
                .await()
            encountersCache.addAll(snapshot.toObjects())
            cacheInitialized = true
        }
        return encountersCache
    }

    suspend fun getEncounter(id: String): Encounter? {
        val encounter = Firebase.firestore
            .collection("encounters")
            .document(id)
            .get()
            .await()
        return encounter.toObject()
    }

    suspend fun createEncounter(
        title: String,
        description: String,
        npcs: List<NPC>,
        chars: ArrayList<Toon>
    ): Encounter {
        val doc = Firebase.firestore.collection("encounters").document()
        val encounter = Encounter(
            title = title,
            description = description,
            id = doc.id,
            userId = UserRepository.getCurrentUserId(),
            npcs = npcs,
            chars = chars
        )
        doc.set(encounter).await()
        encountersCache.add(encounter)
        return encounter
    }

    suspend fun updateEncounter(encounter: Encounter) {
        Firebase.firestore
            .collection("encounters")
            .document(encounter.id!!)
            .set(encounter)
            .await()

        val oldEncounterIndex = encountersCache.indexOfFirst {
            it.id == encounter.id
        }
        encountersCache[oldEncounterIndex] = encounter
    }

    suspend fun deleteEncounter(encounter: Encounter) {
        Firebase.firestore
            .collection("encounters")
            .document(encounter.id!!)
            .delete()
            .await()

        encountersCache.remove(encounter)
    }

    fun isInitialized(): Boolean {
        return cacheInitialized
    }
}
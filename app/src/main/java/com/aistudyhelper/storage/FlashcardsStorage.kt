package com.aistudyhelper.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aistudyhelper.models.Flashcard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.createdFlashcardsDataStore by preferencesDataStore(name = "created_flashcards")

class FlashcardsStorage(private val context: Context) {
    private val flashcardsKey = stringPreferencesKey("created_flashcards_json")
    private val gson = Gson()
    private val storedListType = object : TypeToken<List<StoredFlashcard>>() {}.type

    fun getCreatedFlashcards(subject: String): Flow<List<Flashcard>> = getStoredFlashcards()
        .map { flashcards ->
            flashcards
                .filter { it.subject == subject }
                .map { it.toFlashcard() }
        }

    fun getCreatedCounts(): Flow<Map<String, Int>> = getStoredFlashcards()
        .map { flashcards ->
            flashcards
                .groupingBy { it.subject }
                .eachCount()
        }

    suspend fun addFlashcard(subject: String, question: String, answer: String): Int {
        var createdId = 0

        context.createdFlashcardsDataStore.edit { preferences ->
            val currentFlashcards = decodeFlashcards(preferences[flashcardsKey].orEmpty())
            createdId = ((currentFlashcards.maxOfOrNull { it.id } ?: 9_999) + 1)
            val newFlashcard = StoredFlashcard(
                subject = subject,
                id = createdId,
                question = question.trim(),
                answer = answer.trim()
            )

            preferences[flashcardsKey] = encodeFlashcards(currentFlashcards + newFlashcard)
        }

        return createdId
    }

    suspend fun deleteCreatedFlashcard(subject: String, id: Int) {
        context.createdFlashcardsDataStore.edit { preferences ->
            val currentFlashcards = decodeFlashcards(preferences[flashcardsKey].orEmpty())
            preferences[flashcardsKey] = encodeFlashcards(
                currentFlashcards.filterNot { it.subject == subject && it.id == id }
            )
        }
    }

    private fun getStoredFlashcards(): Flow<List<StoredFlashcard>> {
        return context.createdFlashcardsDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                decodeFlashcards(preferences[flashcardsKey].orEmpty())
            }
    }

    private fun encodeFlashcards(flashcards: List<StoredFlashcard>): String {
        return gson.toJson(flashcards, storedListType)
    }

    private fun decodeFlashcards(rawJson: String): List<StoredFlashcard> {
        if (rawJson.isBlank()) return emptyList()

        return runCatching {
            gson.fromJson<List<StoredFlashcard>>(rawJson, storedListType).orEmpty()
        }.getOrDefault(emptyList())
    }

    private data class StoredFlashcard(
        val subject: String,
        val id: Int,
        val question: String,
        val answer: String
    ) {
        fun toFlashcard(): Flashcard {
            return Flashcard(
                id = id,
                question = question,
                answer = answer,
                isUserCreated = true
            )
        }
    }
}

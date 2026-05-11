package com.aistudyhelper.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aistudyhelper.models.StudyResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.studyResultsDataStore by preferencesDataStore(name = "study_results")

class ResultsStorage(private val context: Context) {
    private val resultsKey = stringPreferencesKey("results_json")
    private val gson = Gson()
    private val resultListType = object : TypeToken<List<StudyResult>>() {}.type

    fun getResults(): Flow<List<StudyResult>> = context.studyResultsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            decodeResults(preferences[resultsKey].orEmpty()).asReversed()
        }

    suspend fun saveResult(result: StudyResult) {
        context.studyResultsDataStore.edit { preferences ->
            val currentResults = decodeResults(preferences[resultsKey].orEmpty())
            preferences[resultsKey] = encodeResults(currentResults + result)
        }
    }

    suspend fun deleteResult(result: StudyResult) {
        context.studyResultsDataStore.edit { preferences ->
            val currentResults = decodeResults(preferences[resultsKey].orEmpty())
            preferences[resultsKey] = encodeResults(currentResults.filterNot { it == result })
        }
    }

    private fun encodeResults(results: List<StudyResult>): String {
        return gson.toJson(results, resultListType)
    }

    private fun decodeResults(rawJson: String): List<StudyResult> {
        if (rawJson.isBlank()) return emptyList()

        return runCatching {
            gson.fromJson<List<StudyResult>>(rawJson, resultListType).orEmpty()
        }.getOrDefault(emptyList())
    }
}

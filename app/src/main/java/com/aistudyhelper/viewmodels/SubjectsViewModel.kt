package com.aistudyhelper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudyhelper.data.SubjectRepository
import com.aistudyhelper.storage.FlashcardsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SubjectsUiState(
    val subjects: List<String> = SubjectRepository.subjects,
    val flashcardCounts: Map<String, Int> = SubjectRepository.subjects.associateWith {
        SubjectRepository.getFlashcards(it).size
    }
) {
    val totalFlashcards: Int = flashcardCounts.values.sum()
    val totalSubjects: Int = subjects.size
}

class SubjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val flashcardsStorage = FlashcardsStorage(application)

    private val _uiState = MutableStateFlow(SubjectsUiState())
    val uiState: StateFlow<SubjectsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            flashcardsStorage.getCreatedCounts().collect { createdCounts ->
                _uiState.update {
                    val customSubjects = createdCounts.keys
                        .filterNot { subject ->
                            SubjectRepository.subjects.any { it.equals(subject, ignoreCase = true) }
                        }
                        .sorted()
                    val subjects = SubjectRepository.subjects + customSubjects

                    SubjectsUiState(
                        subjects = subjects,
                        flashcardCounts = subjects.associateWith { subject ->
                            SubjectRepository.getFlashcards(subject).size + createdCounts.getOrDefault(subject, 0)
                        }
                    )
                }
            }
        }
    }

    fun addTopicWithFirstFlashcard(
        topic: String,
        question: String,
        answer: String,
        onCreated: (String) -> Unit
    ) {
        val cleanTopic = topic.trim()
        val cleanQuestion = question.trim()
        val cleanAnswer = answer.trim()
        if (cleanTopic.isBlank() || cleanQuestion.isBlank() || cleanAnswer.isBlank()) return

        val subject = _uiState.value.subjects.firstOrNull {
            it.equals(cleanTopic, ignoreCase = true)
        } ?: cleanTopic

        viewModelScope.launch {
            flashcardsStorage.addFlashcard(
                subject = subject,
                question = cleanQuestion,
                answer = cleanAnswer
            )
            onCreated(subject)
        }
    }
}

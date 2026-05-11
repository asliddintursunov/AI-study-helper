package com.aistudyhelper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aistudyhelper.data.SubjectRepository
import com.aistudyhelper.models.Flashcard
import com.aistudyhelper.models.StudyResult
import com.aistudyhelper.storage.FlashcardsStorage
import com.aistudyhelper.storage.ResultsStorage
import com.aistudyhelper.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class FlashcardUiState(
    val subject: String,
    val flashcards: List<Flashcard>,
    val currentIndex: Int = 0,
    val showAnswer: Boolean = false,
    val correct: Int = 0,
    val incorrect: Int = 0,
    val isCompleted: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val score: Int = 0,
    val result: StudyResult? = null
) {
    val currentFlashcard: Flashcard? = flashcards.getOrNull(currentIndex)
    val progressText: String = "${(currentIndex + 1).coerceAtMost(flashcards.size)} / ${flashcards.size}"
    val progress: Float = if (flashcards.isEmpty()) {
        0f
    } else {
        ((currentIndex + 1).coerceAtMost(flashcards.size).toFloat() / flashcards.size)
    }
}

class FlashcardViewModel(
    private val application: Application,
    subject: String
) : AndroidViewModel(application) {
    private val defaultFlashcards = SubjectRepository.getFlashcards(subject)
    private val flashcardsStorage = FlashcardsStorage(application)
    private val resultsStorage = ResultsStorage(application)
    private var pendingCreatedFlashcardId: Int? = null

    private val _uiState = MutableStateFlow(
        FlashcardUiState(
            subject = subject,
            flashcards = defaultFlashcards
        )
    )
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            flashcardsStorage.getCreatedFlashcards(subject).collect { createdFlashcards ->
                _uiState.update { state ->
                    val flashcards = defaultFlashcards + createdFlashcards
                    val pendingId = pendingCreatedFlashcardId
                    val selectedIndex = pendingId?.let { id ->
                        flashcards.indexOfFirst { it.id == id && it.isUserCreated }
                            .takeIf { it >= 0 }
                    }
                    val maxIndex = (flashcards.size - 1).coerceAtLeast(0)

                    if (selectedIndex != null) {
                        pendingCreatedFlashcardId = null
                    }

                    state.copy(
                        flashcards = flashcards,
                        currentIndex = selectedIndex ?: state.currentIndex.coerceAtMost(maxIndex),
                        showAnswer = if (flashcards.isEmpty()) false else state.showAnswer
                    )
                }
            }
        }
    }

    fun showAnswer() {
        _uiState.update { it.copy(showAnswer = true) }
    }

    fun addFlashcard(question: String, answer: String) {
        val cleanQuestion = question.trim()
        val cleanAnswer = answer.trim()
        if (cleanQuestion.isBlank() || cleanAnswer.isBlank()) return

        viewModelScope.launch {
            val newId = flashcardsStorage.addFlashcard(
                subject = _uiState.value.subject,
                question = cleanQuestion,
                answer = cleanAnswer
            )
            pendingCreatedFlashcardId = newId
            _uiState.update {
                val selectedIndex = it.flashcards.indexOfFirst { flashcard ->
                    flashcard.id == newId && flashcard.isUserCreated
                }.takeIf { index -> index >= 0 }

                it.copy(
                    currentIndex = selectedIndex ?: it.currentIndex,
                    isCompleted = false,
                    isSaving = false,
                    isSaved = false,
                    result = null,
                    showAnswer = false
                )
            }
        }
    }

    fun deleteCurrentFlashcard() {
        val state = _uiState.value
        val flashcard = state.currentFlashcard ?: return
        if (!flashcard.isUserCreated || state.isSaving) return

        viewModelScope.launch {
            flashcardsStorage.deleteCreatedFlashcard(
                subject = state.subject,
                id = flashcard.id
            )
            _uiState.update {
                it.copy(showAnswer = false)
            }
        }
    }

    fun markAnswer(isCorrect: Boolean) {
        val state = _uiState.value
        if (state.isCompleted || state.isSaving || state.flashcards.isEmpty()) return

        val correct = state.correct + if (isCorrect) 1 else 0
        val incorrect = state.incorrect + if (isCorrect) 0 else 1
        val isLastCard = state.currentIndex == state.flashcards.lastIndex

        if (isLastCard) {
            completeSession(correct, incorrect)
        } else {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    showAnswer = false,
                    correct = correct,
                    incorrect = incorrect
                )
            }
        }
    }

    private fun completeSession(correct: Int, incorrect: Int) {
        val total = correct + incorrect
        val score = if (total == 0) 0 else ((correct.toDouble() / total) * 100).roundToInt()
        val result = StudyResult(
            subject = _uiState.value.subject,
            correct = correct,
            incorrect = incorrect,
            score = score,
            completedAt = DateUtils.now()
        )

        _uiState.update {
            it.copy(
                correct = correct,
                incorrect = incorrect,
                score = score,
                result = result,
                isCompleted = true,
                isSaving = true
            )
        }

        viewModelScope.launch {
            resultsStorage.saveResult(result)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    companion object {
        fun factory(application: Application, subject: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FlashcardViewModel(application, subject) as T
                }
            }
        }
    }
}

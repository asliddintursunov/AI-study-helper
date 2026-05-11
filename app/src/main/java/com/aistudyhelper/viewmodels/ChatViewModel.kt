package com.aistudyhelper.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudyhelper.data.GeminiAiRepository
import com.aistudyhelper.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = 1L,
            text = "Hi, I can help you review study topics.",
            isUser = false
        )
    ),
    val input: String = "",
    val isLoading: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var nextMessageId = 2L

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun sendMessage() {
        val currentState = _uiState.value
        val userText = currentState.input.trim()
        if (userText.isBlank() || currentState.isLoading) return

        val userMessage = ChatMessage(
            id = nextMessageId++,
            text = userText,
            isUser = true
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                input = "",
                isLoading = true
            )
        }

        viewModelScope.launch {
            val aiMessage = ChatMessage(
                id = nextMessageId++,
                text = GeminiAiRepository.createResponse(userText),
                isUser = false
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + aiMessage,
                    isLoading = false
                )
            }
        }
    }
}

package com.aistudyhelper.models

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean
)

package com.aistudyhelper.models

data class Flashcard(
    val id: Int,
    val question: String,
    val answer: String,
    val isUserCreated: Boolean = false
)

package com.aistudyhelper.models

data class StudyResult(
    val subject: String,
    val correct: Int,
    val incorrect: Int,
    val score: Int,
    val completedAt: String
)

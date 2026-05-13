package com.aistudyhelper.data

import com.aistudyhelper.models.Flashcard

object SubjectRepository {
    val subjects = listOf(
        "Math",
        "Physics",
        "Programming",
        "Computer Networks",
        "English"
    )

    private val flashcardsBySubject = mapOf(
        "Math" to listOf(
            Flashcard(1, "What is a derivative?", "A derivative measures the rate of change of a function."),
            Flashcard(2, "What is the value of pi rounded to two decimals?", "Pi is approximately 3.14."),
            Flashcard(3, "What is a prime number?", "A prime number has exactly two factors: 1 and itself."),
            Flashcard(4, "What is 12 x 8?", "12 x 8 equals 96.")
        ),
        "Physics" to listOf(
            Flashcard(1, "What is gravity?", "Gravity is the force of attraction between objects with mass."),
            Flashcard(2, "What is the SI unit of force?", "The SI unit of force is the newton."),
            Flashcard(3, "What does velocity measure?", "Velocity measures speed in a specific direction."),
            Flashcard(4, "What is energy?", "Energy is the ability to do work.")
        ),
        "Programming" to listOf(
            Flashcard(1, "What is a variable?", "A variable stores data that can be used by a program."),
            Flashcard(2, "What is a function?", "A function is a reusable block of code."),
            Flashcard(3, "What is Kotlin?", "Kotlin is a modern programming language used for Android development."),
            Flashcard(4, "What is a list?", "A list is an ordered collection of values.")
        ),
        "Computer Networks" to listOf(
            Flashcard(1, "What is HTTP?", "HTTP stands for HyperText Transfer Protocol."),
            Flashcard(2, "What is an IP address?", "An IP address identifies a device on a network."),
            Flashcard(3, "What is DNS?", "DNS translates domain names into IP addresses."),
            Flashcard(4, "What is TCP?", "TCP is a reliable protocol for sending data between devices.")
        ),
        "English" to listOf(
            Flashcard(1, "What is a noun?", "A noun names a person, place, thing, or idea."),
            Flashcard(2, "What is a synonym?", "A synonym is a word with the same or nearly the same meaning as another word."),
            Flashcard(3, "What does an adjective describe?", "An adjective describes a noun or pronoun."),
            Flashcard(4, "What is the main idea of a paragraph?", "The main idea is the central point or message of the paragraph.")
        )
    )

    fun getFlashcards(subject: String): List<Flashcard> = flashcardsBySubject[subject].orEmpty()
}

package com.aistudyhelper.data

object FakeAiRepository {
    fun createResponse(prompt: String): String {
        val text = prompt.lowercase()
        return when {
            "http" in text -> "HTTP stands for HyperText Transfer Protocol. It is used to transfer web pages and data between clients and servers."
            "recursion" in text || "recursive" in text -> "Recursion is when a function calls itself to solve a smaller version of the same problem. A base case stops the calls."
            "tcp" in text -> "TCP is a reliable transport protocol. It checks delivery and keeps data packets in order."
            "ip" in text -> "IP stands for Internet Protocol. It helps devices address and route packets across networks."
            "variable" in text -> "A variable stores a value that a program can read or change while it runs."
            "function" in text -> "A function is a reusable block of code that performs one clear task."
            "gravity" in text -> "Gravity is the force that attracts objects with mass toward each other."
            "derivative" in text -> "A derivative shows how fast a function changes at a specific point."
            "english" in text || "grammar" in text -> "English practice can include grammar, vocabulary, reading comprehension, and clear writing."
            "study" in text -> "Try studying in short focused sessions, then test yourself with flashcards."
            else -> "This is a simulated AI answer. Review the related subject flashcards to practice the concept."
        }
    }
}

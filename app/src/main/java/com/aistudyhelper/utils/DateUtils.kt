package com.aistudyhelper.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    fun now(): String = LocalDateTime.now().format(formatter)
}

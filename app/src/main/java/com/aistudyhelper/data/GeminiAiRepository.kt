package com.aistudyhelper.data

import com.aistudyhelper.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object GeminiAiRepository {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    private val gson = Gson()

    suspend fun createResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isBlank() || apiKey.contains("PASTE_YOUR", ignoreCase = true)) {
            return@withContext "Gemini is connected, but no API key is configured yet. Add GEMINI_API_KEY to local.properties and run the app again."
        }

        val endpoint = "$BASE_URL/${BuildConfig.GEMINI_MODEL}:generateContent"
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 25_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", apiKey)
        }

        try {
            connection.outputStream.bufferedWriter().use { writer ->
                writer.write(gson.toJson(createRequestBody(prompt)))
            }

            val responseCode = connection.responseCode
            val responseBody = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            }

            if (responseCode !in 200..299) {
                return@withContext "Gemini request failed ($responseCode). Check your API key and internet connection."
            }

            parseTextResponse(responseBody)
                ?: "Gemini returned an empty response. Try asking in a different way."
        } catch (exception: IOException) {
            "I couldn't reach Gemini right now. Please check the emulator internet connection and try again."
        } finally {
            connection.disconnect()
        }
    }

    private fun createRequestBody(prompt: String): JsonObject {
        return JsonObject().apply {
            add(
                "systemInstruction",
                JsonObject().apply {
                    add(
                        "parts",
                        JsonArray().apply {
                            add(JsonObject().apply {
                                addProperty(
                                    "text",
                                    "You are AI Study Helper. Give clear, concise study explanations for university students."
                                )
                            })
                        }
                    )
                }
            )
            add(
                "contents",
                JsonArray().apply {
                    add(
                        JsonObject().apply {
                            addProperty("role", "user")
                            add(
                                "parts",
                                JsonArray().apply {
                                    add(JsonObject().apply { addProperty("text", prompt) })
                                }
                            )
                        }
                    )
                }
            )
            add(
                "generationConfig",
                JsonObject().apply {
                    addProperty("temperature", 0.7)
                    addProperty("maxOutputTokens", 512)
                }
            )
        }
    }

    private fun parseTextResponse(rawJson: String): String? {
        return runCatching {
            val root = JsonParser.parseString(rawJson).asJsonObject
            root.getAsJsonArray("candidates")
                ?.firstOrNull()
                ?.asJsonObject
                ?.getAsJsonObject("content")
                ?.getAsJsonArray("parts")
                ?.firstOrNull()
                ?.asJsonObject
                ?.get("text")
                ?.asString
                ?.trim()
                ?.takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}

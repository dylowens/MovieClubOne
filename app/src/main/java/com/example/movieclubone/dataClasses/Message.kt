package com.example.movieclubone.dataClasses

import com.google.firebase.Timestamp

data class Message(
    val id: String? = "",
    val userId: String? = "",
    val userName: String? = "",
    val message: String? = "",
    val timestamp: Timestamp? = Timestamp.now(),
    val type: String? = "user", // "user" or "bot"
    val photoUrl: String? = "",
    val prompt: String? = null, // For bot prompts
    val response: String? = null // For bot responses
)


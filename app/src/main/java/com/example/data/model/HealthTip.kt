package com.example.data.model

data class HealthTip(
    val id: String,
    val title: String,
    val summary: String,
    val category: String, // "Weight Loss", "Mindset & Vibe", "Metabolism", "Hydration"
    val iconEmoji: String,
    val actionableStep: String,
    val explanation: String = "",
    val isSaved: Boolean = false,
    val isCompletedToday: Boolean = false
)

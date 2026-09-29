package com.example.data.model

data class FoodRecognitionResult(
    val dishName: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val portion: String = "1 standard plate",
    val confidence: Float = 0.92f, // < 0.75 triggers user clarification dialog
    val detectedItems: List<String> = emptyList(),
    val vibeSummary: String = "Healthy fuel for your day",
    val requiresClarification: Boolean = false,
    val clarificationPrompt: String? = null,
    val alternativeSuggestions: List<String> = emptyList()
)

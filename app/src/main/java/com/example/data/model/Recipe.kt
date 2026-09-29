package com.example.data.model

data class Recipe(
    val id: String,
    val title: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val prepTimeMinutes: Int,
    val category: String, // "High Protein", "Low Calorie", "Quick & Fresh", "Comfort Healthy"
    val difficulty: String = "Easy",
    val ingredients: List<String>,
    val instructions: List<String>,
    val vibeTag: String,
    val isFavorite: Boolean = false,
    val imageResId: Int? = null
)

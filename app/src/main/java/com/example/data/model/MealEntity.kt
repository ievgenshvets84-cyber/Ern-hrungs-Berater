package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double = 0.0,
    val portion: String = "1 serving",
    val mealType: String = "Lunch", // Breakfast, Lunch, Dinner, Snack
    val timestamp: Long = System.currentTimeMillis(),
    val dateEpochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24),
    val vibeTone: String = "Nourishing & Balanced",
    val ingredients: String = "",
    val confidence: Float = 0.95f,
    val imagePath: String? = null
)

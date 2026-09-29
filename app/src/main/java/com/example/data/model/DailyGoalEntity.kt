package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoalEntity(
    @PrimaryKey
    val dateEpochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24),
    val calorieTarget: Int = 2000,
    val proteinTarget: Double = 130.0,
    val carbsTarget: Double = 220.0,
    val fatTarget: Double = 65.0,
    val fiberTarget: Double = 30.0,
    val waterMl: Int = 0,
    val waterTargetMl: Int = 2500
)

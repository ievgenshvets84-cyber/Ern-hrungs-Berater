package com.example.data.repository

import com.example.data.db.DailyGoalDao
import com.example.data.db.MealDao
import com.example.data.model.DailyGoalEntity
import com.example.data.model.HealthTip
import com.example.data.model.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class VibeRepository(
    private val mealDao: MealDao,
    private val goalDao: DailyGoalDao
) {
    fun getMealsForDate(dateEpochDay: Long): Flow<List<MealEntity>> {
        return mealDao.getMealsForDate(dateEpochDay)
    }

    fun getAllMeals(): Flow<List<MealEntity>> {
        return mealDao.getAllMeals()
    }

    fun getGoalForDate(dateEpochDay: Long): Flow<DailyGoalEntity?> {
        return goalDao.getGoalForDate(dateEpochDay)
    }

    suspend fun ensureGoalInitialized(dateEpochDay: Long) {
        val existing = goalDao.getGoalForDateDirect(dateEpochDay)
        if (existing == null) {
            goalDao.upsertGoal(
                DailyGoalEntity(
                    dateEpochDay = dateEpochDay,
                    calorieTarget = 2000,
                    proteinTarget = 120.0,
                    carbsTarget = 210.0,
                    fatTarget = 65.0,
                    fiberTarget = 28.0,
                    waterMl = 750,
                    waterTargetMl = 2500
                )
            )
        }
    }

    suspend fun insertMeal(meal: MealEntity): Long {
        return mealDao.insertMeal(meal)
    }

    suspend fun updateMeal(meal: MealEntity) {
        mealDao.updateMeal(meal)
    }

    suspend fun deleteMeal(meal: MealEntity) {
        mealDao.deleteMeal(meal)
    }

    suspend fun addWater(dateEpochDay: Long, deltaMl: Int) {
        val currentGoal = goalDao.getGoalForDateDirect(dateEpochDay) ?: DailyGoalEntity(dateEpochDay = dateEpochDay)
        val newWater = (currentGoal.waterMl + deltaMl).coerceAtLeast(0)
        goalDao.upsertGoal(currentGoal.copy(waterMl = newWater))
    }

    suspend fun updateDailyTargets(dateEpochDay: Long, calories: Int, protein: Double, carbs: Double, fat: Double, waterTarget: Int) {
        val currentGoal = goalDao.getGoalForDateDirect(dateEpochDay) ?: DailyGoalEntity(dateEpochDay = dateEpochDay)
        goalDao.upsertGoal(
            currentGoal.copy(
                calorieTarget = calories,
                proteinTarget = protein,
                carbsTarget = carbs,
                fatTarget = fat,
                waterTargetMl = waterTarget
            )
        )
    }

    suspend fun seedSampleIfEmpty(dateEpochDay: Long) {
        val existing = mealDao.getMealsForDate(dateEpochDay).firstOrNull()
        if (existing.isNullOrEmpty()) {
            // Seed a wholesome starting meal
            mealDao.insertMeal(
                MealEntity(
                    name = "Avocado Toast & Poached Egg",
                    calories = 380,
                    protein = 16.0,
                    carbs = 32.0,
                    fat = 22.0,
                    fiber = 7.0,
                    portion = "2 slices with 1 egg",
                    mealType = "Breakfast",
                    vibeTone = "Fresh Mindful Start ✨",
                    ingredients = "Sourdough bread, ripe avocado, poached egg, chili flakes, olive oil",
                    confidence = 0.98f
                )
            )
            ensureGoalInitialized(dateEpochDay)
        }
    }

    val healthTipsList: List<HealthTip> = listOf(
        HealthTip(
            id = "tip_1",
            title = "Volume Eating for Satiety",
            summary = "Prioritize high-water, high-fiber foods like leafy greens, cucumbers, and berries to stay full while keeping calories naturally balanced.",
            category = "Weight Loss",
            iconEmoji = "🥗",
            actionableStep = "Add a handful of fresh spinach or steamed greens to your next meal."
        ),
        HealthTip(
            id = "tip_2",
            title = "The Power of Protein Pacing",
            summary = "Aiming for 25–35g of protein per meal helps preserve lean muscle, keeps cravings low, and stabilizes your energy vibes.",
            category = "Metabolism",
            iconEmoji = "⚡",
            actionableStep = "Include Greek yogurt, eggs, tofu, fish, or lean meat at your next plate."
        ),
        HealthTip(
            id = "tip_3",
            title = "Mindful Plate Checking",
            summary = "Taking a 5-second breath before your first bite activates parasympathetic digestion, enhancing nutritional absorption.",
            category = "Mindset & Vibe",
            iconEmoji = "🌿",
            actionableStep = "Look at your plate, appreciate the colors, and chew slowly."
        ),
        HealthTip(
            id = "tip_4",
            title = "Hydration Before Cravings",
            summary = "Mild dehydration is often misinterpreted by the brain as sugar or carb hunger.",
            category = "Hydration",
            iconEmoji = "💧",
            actionableStep = "Drink a 250ml glass of water 15 minutes before reaching for snacks."
        ),
        HealthTip(
            id = "tip_5",
            title = "Gentle Calorie Deficit",
            summary = "Sustainable fat loss flourishes with a 300–400 kcal deficit rather than crash dieting, keeping your metabolic rate resilient.",
            category = "Weight Loss",
            iconEmoji = "🎯",
            actionableStep = "Focus on consistency and feeling energized, not extreme restriction."
        )
    )
}

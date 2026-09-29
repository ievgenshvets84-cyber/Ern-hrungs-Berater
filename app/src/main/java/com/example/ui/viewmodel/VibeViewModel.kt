package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiFoodService
import com.example.data.db.AppDatabase
import com.example.data.model.DailyGoalEntity
import com.example.data.model.FoodRecognitionResult
import com.example.data.model.HealthTip
import com.example.data.model.MealEntity
import com.example.data.model.Recipe
import com.example.data.repository.RecipeRepository
import com.example.data.repository.VibeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScannerUiState(
    val isScanning: Boolean = false,
    val scannedBitmap: Bitmap? = null,
    val result: FoodRecognitionResult? = null,
    val showClarificationDialog: Boolean = false,
    val clarificationDishName: String = "",
    val clarificationIngredients: String = "",
    val clarificationCalories: Int = 0,
    val clarificationProtein: Double = 0.0,
    val clarificationCarbs: Double = 0.0,
    val clarificationFat: Double = 0.0,
    val clarificationFiber: Double = 0.0,
    val clarificationPortion: String = "1 standard plate",
    val clarificationMealType: String = "Lunch"
)

@OptIn(ExperimentalCoroutinesApi::class)
class VibeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = VibeRepository(db.mealDao(), db.dailyGoalDao())
    private val geminiFoodService = GeminiFoodService()

    private val currentDayEpoch = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
    private val _selectedEpochDay = MutableStateFlow(currentDayEpoch)
    val selectedEpochDay: StateFlow<Long> = _selectedEpochDay.asStateFlow()

    val mealsForSelectedDay: StateFlow<List<MealEntity>> = _selectedEpochDay
        .flatMapLatest { epoch -> repository.getMealsForDate(epoch) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyGoalForSelectedDay: StateFlow<DailyGoalEntity?> = _selectedEpochDay
        .flatMapLatest { epoch -> repository.getGoalForDate(epoch) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyTotals = combine(mealsForSelectedDay, dailyGoalForSelectedDay) { meals, goal ->
        val totalCals = meals.sumOf { it.calories }
        val totalProt = meals.sumOf { it.protein }
        val totalCarb = meals.sumOf { it.carbs }
        val totalFatVal = meals.sumOf { it.fat }
        val totalFib = meals.sumOf { it.fiber }

        val targetCals = goal?.calorieTarget ?: 2000
        val remainingCals = (targetCals - totalCals).coerceAtLeast(0)
        val calorieProgress = if (targetCals > 0) (totalCals.toFloat() / targetCals.toFloat()).coerceIn(0f, 1.2f) else 0f

        // Vibe Score (0 to 100)
        val calAdherence = 100 - (kotlin.math.abs(totalCals - targetCals) * 100 / targetCals).coerceIn(0, 100)
        val waterAdherence = if ((goal?.waterTargetMl ?: 2500) > 0) {
            ((goal?.waterMl ?: 0) * 100 / (goal?.waterTargetMl ?: 2500)).coerceIn(0, 100)
        } else 50
        val computedVibeScore = ((calAdherence * 0.6) + (waterAdherence * 0.4)).toInt().coerceIn(30, 99)

        val vibeStatus = when {
            computedVibeScore >= 85 -> "Peak Radiance ✨"
            computedVibeScore >= 70 -> "Balanced & Grounded 🌿"
            computedVibeScore >= 50 -> "Active Mindful Flow 🌊"
            else -> "Gentle Nourishment 🌱"
        }

        DailyNutritionalSummary(
            totalCalories = totalCals,
            remainingCalories = remainingCals,
            targetCalories = targetCals,
            calorieProgress = calorieProgress,
            totalProtein = totalProt,
            targetProtein = goal?.proteinTarget ?: 120.0,
            totalCarbs = totalCarb,
            targetCarbs = goal?.carbsTarget ?: 210.0,
            totalFat = totalFatVal,
            targetFat = goal?.fatTarget ?: 65.0,
            totalFiber = totalFib,
            targetFiber = goal?.fiberTarget ?: 28.0,
            waterMl = goal?.waterMl ?: 0,
            waterTargetMl = goal?.waterTargetMl ?: 2500,
            vibeScore = computedVibeScore,
            vibeStatus = vibeStatus
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DailyNutritionalSummary()
    )

    private val recipeRepository = RecipeRepository()
    val recipes: StateFlow<List<Recipe>> = recipeRepository.recipes

    // Interactive Health Tips
    private val _healthTipsState = MutableStateFlow(repository.healthTipsList)
    val healthTipsState: StateFlow<List<HealthTip>> = _healthTipsState.asStateFlow()

    private val _currentTipIndex = MutableStateFlow(0)
    val currentTipIndex = _currentTipIndex.asStateFlow()

    // AI Coach State
    private val _coachAnswer = MutableStateFlow<String?>(null)
    val coachAnswer: StateFlow<String?> = _coachAnswer.asStateFlow()

    private val _isCoachLoading = MutableStateFlow(false)
    val isCoachLoading: StateFlow<Boolean> = _isCoachLoading.asStateFlow()

    private val _isGeneratingTip = MutableStateFlow(false)
    val isGeneratingTip: StateFlow<Boolean> = _isGeneratingTip.asStateFlow()

    // Scanner state
    private val _scannerUiState = MutableStateFlow(ScannerUiState())
    val scannerUiState: StateFlow<ScannerUiState> = _scannerUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureGoalInitialized(currentDayEpoch)
            repository.seedSampleIfEmpty(currentDayEpoch)
        }
    }

    fun toggleRecipeFavorite(recipeId: String) {
        recipeRepository.toggleFavorite(recipeId)
    }

    fun logRecipeToMeals(recipe: Recipe, mealType: String = "Lunch") {
        viewModelScope.launch {
            repository.insertMeal(
                MealEntity(
                    name = recipe.title,
                    calories = recipe.calories,
                    protein = recipe.protein,
                    carbs = recipe.carbs,
                    fat = recipe.fat,
                    fiber = recipe.fiber,
                    portion = "1 Rezept-Portion",
                    mealType = mealType,
                    vibeTone = recipe.vibeTag,
                    ingredients = recipe.ingredients.joinToString(", "),
                    confidence = 1.0f
                )
            )
        }
    }

    fun addCustomRecipe(recipe: Recipe) {
        recipeRepository.addCustomRecipe(recipe)
    }

    fun toggleTipSaved(tipId: String) {
        _healthTipsState.value = _healthTipsState.value.map {
            if (it.id == tipId) it.copy(isSaved = !it.isSaved) else it
        }
    }

    fun toggleTipCompletedToday(tipId: String) {
        _healthTipsState.value = _healthTipsState.value.map {
            if (it.id == tipId) it.copy(isCompletedToday = !it.isCompletedToday) else it
        }
    }

    fun generateNewAiHealthTip(category: String = "Weight Loss") {
        viewModelScope.launch {
            _isGeneratingTip.value = true
            val summary = dailyTotals.value
            val context = "${summary.totalCalories}/${summary.targetCalories} kcal, ${summary.totalProtein.toInt()}g Protein, Vibe: ${summary.vibeStatus}"
            val newTip = geminiFoodService.generateDynamicHealthTip(category, context)
            _healthTipsState.value = listOf(newTip) + _healthTipsState.value
            _currentTipIndex.value = 0
            _isGeneratingTip.value = false
        }
    }

    fun askCoach(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _isCoachLoading.value = true
            val summary = dailyTotals.value
            val context = "Heutige Aufnahme: ${summary.totalCalories} kcal von ${summary.targetCalories} kcal, ${summary.totalProtein.toInt()}g Protein, ${summary.waterMl}ml Wasser"
            val answer = geminiFoodService.askCoach(question, context)
            _coachAnswer.value = answer
            _isCoachLoading.value = false
        }
    }

    fun nextTip() {
        val tips = _healthTipsState.value
        if (tips.isNotEmpty()) {
            _currentTipIndex.value = (_currentTipIndex.value + 1) % tips.size
        }
    }

    fun addWater(deltaMl: Int) {
        viewModelScope.launch {
            repository.addWater(_selectedEpochDay.value, deltaMl)
        }
    }

    fun updateDailyGoals(calories: Int, protein: Double, carbs: Double, fat: Double, waterTarget: Int) {
        viewModelScope.launch {
            repository.updateDailyTargets(
                dateEpochDay = _selectedEpochDay.value,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                waterTarget = waterTarget
            )
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun selectDate(epochDay: Long) {
        _selectedEpochDay.value = epochDay
        viewModelScope.launch {
            repository.ensureGoalInitialized(epochDay)
        }
    }

    fun scanDish(bitmap: Bitmap?, hint: String? = null, mealType: String = "Lunch") {
        viewModelScope.launch {
            _scannerUiState.value = _scannerUiState.value.copy(
                isScanning = true,
                scannedBitmap = bitmap
            )

            val result = geminiFoodService.analyzeFood(bitmap, hint)

            // Per Master prompt: if uncertain (requiresClarification or confidence < 0.75), ask user for dish name / confirmation
            val shouldClarify = result.requiresClarification || result.confidence < 0.75f

            _scannerUiState.value = ScannerUiState(
                isScanning = false,
                scannedBitmap = bitmap,
                result = result,
                showClarificationDialog = shouldClarify,
                clarificationDishName = result.dishName,
                clarificationIngredients = result.detectedItems.joinToString(", "),
                clarificationCalories = result.calories,
                clarificationProtein = result.protein,
                clarificationCarbs = result.carbs,
                clarificationFat = result.fat,
                clarificationFiber = result.fiber,
                clarificationPortion = result.portion,
                clarificationMealType = mealType
            )
        }
    }

    fun updateClarificationDishName(newName: String) {
        _scannerUiState.value = _scannerUiState.value.copy(clarificationDishName = newName)
    }

    fun updateClarificationIngredients(newIngredients: String) {
        _scannerUiState.value = _scannerUiState.value.copy(clarificationIngredients = newIngredients)
    }

    fun updateClarificationCalories(calories: Int) {
        _scannerUiState.value = _scannerUiState.value.copy(clarificationCalories = calories)
    }

    fun updateClarificationMealType(type: String) {
        _scannerUiState.value = _scannerUiState.value.copy(clarificationMealType = type)
    }

    fun dismissClarification() {
        _scannerUiState.value = _scannerUiState.value.copy(showClarificationDialog = false)
    }

    fun applyAlternativeDish(dishName: String) {
        viewModelScope.launch {
            _scannerUiState.value = _scannerUiState.value.copy(isScanning = true)
            val result = geminiFoodService.analyzeFood(null, dishName)
            _scannerUiState.value = _scannerUiState.value.copy(
                isScanning = false,
                result = result,
                clarificationDishName = result.dishName,
                clarificationIngredients = result.detectedItems.joinToString(", "),
                clarificationCalories = result.calories,
                clarificationProtein = result.protein,
                clarificationCarbs = result.carbs,
                clarificationFat = result.fat,
                clarificationFiber = result.fiber,
                clarificationPortion = result.portion
            )
        }
    }

    fun confirmAndLogScannedMeal() {
        val state = _scannerUiState.value
        val dish = state.clarificationDishName.ifBlank { state.result?.dishName ?: "Logged Dish" }
        val cals = if (state.clarificationCalories > 0) state.clarificationCalories else (state.result?.calories ?: 450)
        val prot = if (state.clarificationProtein > 0) state.clarificationProtein else (state.result?.protein ?: 20.0)
        val carbs = if (state.clarificationCarbs > 0) state.clarificationCarbs else (state.result?.carbs ?: 45.0)
        val fat = if (state.clarificationFat > 0) state.clarificationFat else (state.result?.fat ?: 15.0)
        val fib = if (state.clarificationFiber > 0) state.clarificationFiber else (state.result?.fiber ?: 4.0)
        val vibe = state.result?.vibeSummary ?: "Empowering balanced fuel"

        viewModelScope.launch {
            repository.insertMeal(
                MealEntity(
                    name = dish,
                    calories = cals,
                    protein = prot,
                    carbs = carbs,
                    fat = fat,
                    fiber = fib,
                    portion = state.clarificationPortion,
                    mealType = state.clarificationMealType,
                    vibeTone = vibe,
                    ingredients = state.clarificationIngredients,
                    confidence = state.result?.confidence ?: 0.9f
                )
            )

            // Reset scanner
            _scannerUiState.value = ScannerUiState()
        }
    }

    fun quickLogPreset(name: String, calories: Int, protein: Double, carbs: Double, fat: Double, mealType: String, vibe: String) {
        viewModelScope.launch {
            repository.insertMeal(
                MealEntity(
                    name = name,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    fiber = 4.0,
                    portion = "1 standard serving",
                    mealType = mealType,
                    vibeTone = vibe,
                    confidence = 1.0f
                )
            )
        }
    }
}

data class DailyNutritionalSummary(
    val totalCalories: Int = 0,
    val remainingCalories: Int = 2000,
    val targetCalories: Int = 2000,
    val calorieProgress: Float = 0f,
    val totalProtein: Double = 0.0,
    val targetProtein: Double = 120.0,
    val totalCarbs: Double = 0.0,
    val targetCarbs: Double = 210.0,
    val totalFat: Double = 0.0,
    val targetFat: Double = 65.0,
    val totalFiber: Double = 0.0,
    val targetFiber: Double = 28.0,
    val waterMl: Int = 0,
    val waterTargetMl: Int = 2500,
    val vibeScore: Int = 88,
    val vibeStatus: String = "Peak Radiance ✨"
)

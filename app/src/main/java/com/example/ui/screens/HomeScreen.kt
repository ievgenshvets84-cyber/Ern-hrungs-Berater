package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HealthTipCard
import com.example.ui.components.MacroItem
import com.example.ui.components.MealItemCard
import com.example.ui.components.VibeScoreCard
import com.example.ui.components.WaterTrackerCard
import com.example.ui.theme.CarbsAccent
import com.example.ui.theme.FatAccent
import com.example.ui.theme.ProteinAccent
import com.example.ui.viewmodel.VibeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: VibeViewModel,
    onNavigateToScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dailyTotals.collectAsState()
    val meals by viewModel.mealsForSelectedDay.collectAsState()
    val selectedEpochDay by viewModel.selectedEpochDay.collectAsState()
    val allTips by viewModel.healthTipsState.collectAsState()
    val tipIndex by viewModel.currentTipIndex.collectAsState()
    val healthTip = if (allTips.isNotEmpty()) allTips[tipIndex % allTips.size] else null

    val dateMillis = selectedEpochDay * (1000L * 60 * 60 * 24)
    val dateFormatted = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date(dateMillis))
    val todayEpoch = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
    val isToday = (selectedEpochDay == todayEpoch)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.selectDate(selectedEpochDay - 1) },
                    modifier = Modifier.testTag("prev_day_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .clickable { viewModel.selectDate(todayEpoch) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isToday) "Today ($dateFormatted)" else dateFormatted,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.selectDate(selectedEpochDay + 1) },
                    modifier = Modifier.testTag("next_day_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Vibe Score & Remaining Calories Card
        item {
            VibeScoreCard(
                totalCalories = summary.totalCalories,
                remainingCalories = summary.remainingCalories,
                targetCalories = summary.targetCalories,
                calorieProgress = summary.calorieProgress,
                vibeScore = summary.vibeScore,
                vibeStatus = summary.vibeStatus
            )
        }

        // Macro Nutrients Breakdown Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroItem(
                    label = "Protein",
                    current = summary.totalProtein,
                    target = summary.targetProtein,
                    color = ProteinAccent,
                    modifier = Modifier.weight(1f)
                )
                MacroItem(
                    label = "Carbs",
                    current = summary.totalCarbs,
                    target = summary.targetCarbs,
                    color = CarbsAccent,
                    modifier = Modifier.weight(1f)
                )
                MacroItem(
                    label = "Fat",
                    current = summary.totalFat,
                    target = summary.targetFat,
                    color = FatAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Actionable Rapid Food Logging Action Card (Homescreen Widget style per prompt)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_scanner_widget_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Plate Recognition",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Snap any plate or traditional dish for instant calories & vibe analysis",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = onNavigateToScanner,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("open_scanner_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Scan",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Dish", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "RAPID LOG PRESETS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Dish Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetChip(
                            name = "German Currywurst",
                            cals = "680 kcal",
                            onClick = {
                                viewModel.quickLogPreset(
                                    name = "German Currywurst with Fries",
                                    calories = 680,
                                    protein = 24.0,
                                    carbs = 62.0,
                                    fat = 38.0,
                                    mealType = "Lunch",
                                    vibe = "Comforting German Classic 🌭"
                                )
                            }
                        )
                        PresetChip(
                            name = "Avocado Salmon Bowl",
                            cals = "520 kcal",
                            onClick = {
                                viewModel.quickLogPreset(
                                    name = "Avocado Salmon Nourish Bowl",
                                    calories = 520,
                                    protein = 34.0,
                                    carbs = 42.0,
                                    fat = 24.0,
                                    mealType = "Dinner",
                                    vibe = "Vibrant Omega-3 Glow 🥗"
                                )
                            }
                        )
                        PresetChip(
                            name = "Espresso & Oats",
                            cals = "290 kcal",
                            onClick = {
                                viewModel.quickLogPreset(
                                    name = "Espresso & Warm Berry Oats",
                                    calories = 290,
                                    protein = 11.0,
                                    carbs = 48.0,
                                    fat = 6.0,
                                    mealType = "Breakfast",
                                    vibe = "Focused Morning Energy ☕"
                                )
                            }
                        )
                        PresetChip(
                            name = "Whey Protein Shake",
                            cals = "180 kcal",
                            onClick = {
                                viewModel.quickLogPreset(
                                    name = "Whey Protein Shake with Almond Milk",
                                    calories = 180,
                                    protein = 28.0,
                                    carbs = 5.0,
                                    fat = 3.0,
                                    mealType = "Snack",
                                    vibe = "Clean Muscle Fuel ⚡"
                                )
                            }
                        )
                    }
                }
            }
        }

        // Daily Hydration Widget
        item {
            WaterTrackerCard(
                currentMl = summary.waterMl,
                targetMl = summary.waterTargetMl,
                onAddWater = { delta -> viewModel.addWater(delta) }
            )
        }

        // Daily Health Tip (Engagement & Health Insights per prompt)
        if (healthTip != null) {
            item {
                HealthTipCard(
                    tip = healthTip,
                    onNextTip = { viewModel.nextTip() },
                    onToggleSave = { viewModel.toggleTipSaved(healthTip.id) },
                    onToggleComplete = { viewModel.toggleTipCompletedToday(healthTip.id) }
                )
            }
        }

        // Today's Meals Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Meals (${meals.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${summary.totalCalories} kcal total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (meals.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No meals logged yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Scan Dish' to capture a plate with AI or select a quick preset above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(meals, key = { it.id }) { meal ->
                MealItemCard(
                    meal = meal,
                    onDelete = { viewModel.deleteMeal(meal) }
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    name: String,
    cals: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("preset_${name.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(13.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = cals,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

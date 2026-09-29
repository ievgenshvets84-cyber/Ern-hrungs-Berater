package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.CalorieAccent
import com.example.ui.theme.CarbsAccent
import com.example.ui.theme.FatAccent
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.ProteinAccent
import com.example.ui.theme.WaterAccent
import com.example.ui.viewmodel.VibeViewModel

@Composable
fun SettingsScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val dailyGoal by viewModel.dailyGoalForSelectedDay.collectAsState()

    var calorieTarget by remember(dailyGoal) { mutableStateOf((dailyGoal?.calorieTarget ?: 2000).toString()) }
    var proteinTarget by remember(dailyGoal) { mutableStateOf((dailyGoal?.proteinTarget ?: 120.0).toInt().toString()) }
    var carbsTarget by remember(dailyGoal) { mutableStateOf((dailyGoal?.carbsTarget ?: 210.0).toInt().toString()) }
    var fatTarget by remember(dailyGoal) { mutableStateOf((dailyGoal?.fatTarget ?: 65.0).toInt().toString()) }
    var waterTarget by remember(dailyGoal) { mutableStateOf((dailyGoal?.waterTargetMl ?: 2500).toString()) }

    var selectedGoalType by remember { mutableStateOf("Weight Loss (Deficit)") }
    var savedSuccess by remember { mutableStateOf(false) }

    val hasCustomKey = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nutrition Targets & Vibe Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure your daily calorie goals and personalized nutrition philosophy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Goal Mode Preset Chips
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRIMARY GOAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val presets = listOf(
                        "Weight Loss (Deficit)" to (1800 to 135),
                        "Maintain & Tone" to (2100 to 125),
                        "Lean Muscle Gain" to (2450 to 150)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { (name, targets) ->
                            FilterChip(
                                selected = (selectedGoalType == name),
                                onClick = {
                                    selectedGoalType = name
                                    calorieTarget = targets.first.toString()
                                    proteinTarget = targets.second.toString()
                                },
                                label = { Text(name, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("preset_goal_$name")
                            )
                        }
                    }
                }
            }
        }

        // Daily Targets Input Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DAILY TARGETS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = calorieTarget,
                        onValueChange = { calorieTarget = it },
                        label = { Text("Daily Calorie Target (kcal)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = CalorieAccent)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("calorie_target_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = proteinTarget,
                            onValueChange = { proteinTarget = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("protein_target_input")
                        )
                        OutlinedTextField(
                            value = carbsTarget,
                            onValueChange = { carbsTarget = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("carbs_target_input")
                        )
                        OutlinedTextField(
                            value = fatTarget,
                            onValueChange = { fatTarget = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("fat_target_input")
                        )
                    }

                    OutlinedTextField(
                        value = waterTarget,
                        onValueChange = { waterTarget = it },
                        label = { Text("Daily Water Goal (ml)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = WaterAccent)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("water_target_input")
                    )

                    Button(
                        onClick = {
                            val cals = calorieTarget.toIntOrNull() ?: 2000
                            val prot = proteinTarget.toDoubleOrNull() ?: 120.0
                            val carb = carbsTarget.toDoubleOrNull() ?: 210.0
                            val fat = fatTarget.toDoubleOrNull() ?: 65.0
                            val water = waterTarget.toIntOrNull() ?: 2500
                            viewModel.updateDailyGoals(cals, prot, carb, fat, water)
                            savedSuccess = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_targets_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (savedSuccess) "Targets Saved Successfully!" else "Save Targets", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AI Engine & API Status Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Gemini AI Vision Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (hasCustomKey) HealthGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (hasCustomKey) "Online API Active" else "Smart Engine Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hasCustomKey) HealthGreen else MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vibe Calorie AI seamlessly recognizes dishes with multimodal computer vision. If GEMINI_API_KEY is configured in the AI Studio Secrets panel, live API queries are dispatched; otherwise the smart local culinary engine estimates macros and clarifies dishes instantly without friction.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Philosophy & Tone Note
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "“Vibe Coding” Philosophy & Tone", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The vibe: smooth, intelligent, empathetic, positive, minimalist. Every interaction celebrates mindful nourishing without guilt or anxiety.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

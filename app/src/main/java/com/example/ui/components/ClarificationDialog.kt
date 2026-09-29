package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodRecognitionResult
import com.example.ui.theme.CalorieAccent
import com.example.ui.theme.CarbsAccent
import com.example.ui.theme.FatAccent
import com.example.ui.theme.ProteinAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClarificationDialog(
    dishName: String,
    onDishNameChange: (String) -> Unit,
    ingredients: String,
    onIngredientsChange: (String) -> Unit,
    calories: Int,
    onCaloriesChange: (Int) -> Unit,
    mealType: String,
    onMealTypeChange: (String) -> Unit,
    recognitionResult: FoodRecognitionResult?,
    onSelectAlternative: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("clarification_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Rezept bestätigen?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Unsere KI hat dieses Gericht erkannt. Bitte überprüfe oder passe Name und Zutaten für genaue Nährwerte an:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Editable Dish Name
                OutlinedTextField(
                    value = dishName,
                    onValueChange = onDishNameChange,
                    label = { Text("Gericht / Dish Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clarification_dish_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick alternative suggestion pills
                if (recognitionResult != null && recognitionResult.alternativeSuggestions.isNotEmpty()) {
                    Text(
                        text = "Vorschläge:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        recognitionResult.alternativeSuggestions.forEach { alt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { onSelectAlternative(alt) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = alt,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Ingredients / Notes
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = onIngredientsChange,
                    label = { Text("Zutaten & Beilagen (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clarification_ingredients_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Meal Type Selector
                Text(
                    text = "Mahlzeit:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    mealTypes.forEach { type ->
                        FilterChip(
                            selected = (mealType == type),
                            onClick = { onMealTypeChange(type) },
                            label = { Text(type, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("meal_type_chip_$type")
                        )
                    }
                }

                // Estimated Calories & Macros Preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Geschätzte Energie:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$calories kcal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CalorieAccent
                            )
                        }

                        if (recognitionResult != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "P: ${recognitionResult.protein.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProteinAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "C: ${recognitionResult.carbs.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CarbsAccent,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "F: ${recognitionResult.fat.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FatAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_clarification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "OK / Bestätigen")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_clarification_button")
            ) {
                Text(text = "Cancel / Abbrechen")
            }
        }
    )
}

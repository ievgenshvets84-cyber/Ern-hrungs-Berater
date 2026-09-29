package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Recipe
import com.example.ui.theme.CalorieAccent
import com.example.ui.theme.CarbsAccent
import com.example.ui.theme.FatAccent
import com.example.ui.theme.ProteinAccent
import com.example.ui.viewmodel.VibeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeBookScreen(
    viewModel: VibeViewModel,
    modifier: Modifier = Modifier
) {
    val recipes by viewModel.recipes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Alle") }
    var detailedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var recipeToLog by remember { mutableStateOf<Recipe?>(null) }
    var showAiRecipeDialog by remember { mutableStateOf(false) }

    val categories = listOf("Alle", "High Protein", "Low Calorie", "Comfort Healthy", "Quick & Fresh", "Favoriten")

    val filteredRecipes = recipes.filter { recipe ->
        val matchesCategory = when (selectedCategory) {
            "Alle" -> true
            "Favoriten" -> recipe.isFavorite
            else -> recipe.category.equals(selectedCategory, ignoreCase = true)
        }
        val matchesSearch = searchQuery.isBlank() ||
                recipe.title.contains(searchQuery, ignoreCase = true) ||
                recipe.ingredients.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    // Modal Sheet for Recipe Detail View
    if (detailedRecipe != null) {
        val rec = detailedRecipe!!
        ModalBottomSheet(
            onDismissRequest = { detailedRecipe = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rec.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.toggleRecipeFavorite(rec.id) }) {
                        Icon(
                            imageVector = if (rec.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = if (rec.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${rec.category} • ${rec.prepTimeMinutes} Min Zubereitung • ${rec.vibeTag}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Nutrition Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecipeMacroPill("Kalorien", "${rec.calories} kcal", CalorieAccent, Modifier.weight(1f))
                    RecipeMacroPill("Protein", "${rec.protein.toInt()}g", ProteinAccent, Modifier.weight(1f))
                    RecipeMacroPill("Carbs", "${rec.carbs.toInt()}g", CarbsAccent, Modifier.weight(1f))
                    RecipeMacroPill("Fett", "${rec.fat.toInt()}g", FatAccent, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Ingredients List
                Text(
                    text = "ZUTATEN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                rec.ingredients.forEach { ing ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = ing, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Instructions Steps
                Text(
                    text = "ZUBEREITUNG",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                rec.instructions.forEachIndexed { idx, step ->
                    Row(
                        modifier = Modifier.padding(vertical = 5.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${idx + 1}.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(22.dp)
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button: Log to Today
                Button(
                    onClick = {
                        recipeToLog = rec
                        detailedRecipe = null
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "In Tagesplan eintragen (+${rec.calories} kcal)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Log to Today's Meals Dialog (Pick Meal Type)
    if (recipeToLog != null) {
        val rec = recipeToLog!!
        var chosenMealType by remember { mutableStateOf("Lunch") }
        var loggedSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { recipeToLog = null },
            title = { Text("Rezept in Tagesplan loggen") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Gericht: ${rec.title}")
                    Text(text = "Nährwerte: ${rec.calories} kcal • P: ${rec.protein.toInt()}g • C: ${rec.carbs.toInt()}g • F: ${rec.fat.toInt()}g")

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Mahlzeit auswählen:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                            FilterChip(
                                selected = chosenMealType == type,
                                onClick = { chosenMealType = type },
                                label = { Text(type, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logRecipeToMeals(rec, chosenMealType)
                        recipeToLog = null
                    }
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Jetzt eintragen")
                }
            },
            dismissButton = {
                TextButton(onClick = { recipeToLog = null }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    // AI Custom Recipe Creator Dialog
    if (showAiRecipeDialog) {
        var fridgeIngredients by remember { mutableStateOf("") }
        var generatedRecipeTitle by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAiRecipeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KI-Rezept kreieren")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Gib Zutaten ein, die du zuhause hast (z.B. Eier, Tomaten, Spinat, Haferflocken). Die KI erstellt daraus ein gesundes Rezept mit Makro-Berechnung!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = fridgeIngredients,
                        onValueChange = { fridgeIngredients = it },
                        label = { Text("Zutaten im Kühlschrank") },
                        placeholder = { Text("z.B. Hähnchen, Paprika, Reis") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fridgeIngredients.isNotBlank()) {
                            val custom = Recipe(
                                id = "ai_rec_${System.currentTimeMillis()}",
                                title = "Power-Pfanne mit ${fridgeIngredients.take(20)}",
                                calories = 430,
                                protein = 35.0,
                                carbs = 38.0,
                                fat = 14.0,
                                fiber = 6.0,
                                prepTimeMinutes = 15,
                                category = "High Protein",
                                difficulty = "Einfach",
                                vibeTag = "Kühlschrank-Zauber ✨",
                                ingredients = fridgeIngredients.split(",").map { it.trim() } + listOf("1 TL Olivenöl", "Salz & Pfeffer"),
                                instructions = listOf(
                                    "Zutaten mundgerecht zerkleinern.",
                                    "Pfanne mit 1 TL Olivenöl erhitzen, Hauptzutaten anbraten und würzen.",
                                    "Warm servieren und genießen!"
                                )
                            )
                            viewModel.addCustomRecipe(custom)
                            showAiRecipeDialog = false
                            detailedRecipe = custom
                        }
                    }
                ) {
                    Text("Rezept generieren")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiRecipeDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("recipe_book_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Gesundes Rezeptbuch 📖",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kalorienbewusste, leckere Rezepte zum Nachkochen und 1-Tap Loggen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // AI Recipe Generator Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth().clickable { showAiRecipeDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Rezept aus Kühlschrank-Zutaten?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Lass die KI ein Gericht mit deinen vorhandenen Lebensmitteln kreieren.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Button(
                        onClick = { showAiRecipeDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("open_ai_recipe_button")
                    ) {
                        Text("Kreieren", fontSize = 12.sp)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rezept oder Zutat suchen (z.B. Lachs, Currywurst, Zucchini)...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("recipe_search_input")
            )
        }

        // Category Filter Chips
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("recipe_category_$cat")
                    )
                }
            }
        }

        // Recipes Grid/List
        if (filteredRecipes.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Keine passenden Rezepte gefunden", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Probiere einen anderen Suchbegriff oder eine andere Kategorie.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredRecipes, key = { it.id }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onOpenDetail = { detailedRecipe = recipe },
                    onToggleFavorite = { viewModel.toggleRecipeFavorite(recipe.id) },
                    onLogToMeals = { recipeToLog = recipe }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onOpenDetail: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLogToMeals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
            .testTag("recipe_card_${recipe.id}")
    ) {
        Column {
            // Optional Header Image
            if (recipe.imageResId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = recipe.imageResId),
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (recipe.isFavorite) Color.Red else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    if (recipe.imageResId == null) {
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (recipe.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.prepTimeMinutes} Min • ${recipe.category} • ${recipe.vibeTag}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Macro Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RecipeMiniBadge(text = "${recipe.calories} kcal", color = CalorieAccent)
                    RecipeMiniBadge(text = "${recipe.protein.toInt()}g P", color = ProteinAccent)
                    RecipeMiniBadge(text = "${recipe.carbs.toInt()}g C", color = CarbsAccent)
                    RecipeMiniBadge(text = "${recipe.fat.toInt()}g F", color = FatAccent)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Details & 1-Tap Log
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenDetail,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text(text = "Rezept ansehen", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onLogToMeals,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+ Loggen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeMacroPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
            Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecipeMiniBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
    }
}

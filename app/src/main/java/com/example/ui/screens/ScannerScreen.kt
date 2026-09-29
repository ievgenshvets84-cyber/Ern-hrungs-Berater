package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ClarificationDialog
import com.example.ui.theme.CalorieAccent
import com.example.ui.theme.CarbsAccent
import com.example.ui.theme.FatAccent
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.ProteinAccent
import com.example.ui.viewmodel.VibeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: VibeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scannerState by viewModel.scannerUiState.collectAsState()

    var manualDishInput by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Lunch") }
    var currentPreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentDrawableRes by remember { mutableStateOf<Int?>(null) }
    var showLiveCamera by remember { mutableStateOf(false) }

    if (showLiveCamera) {
        CameraCaptureScreen(
            onPhotoCaptured = { bitmap ->
                currentPreviewBitmap = bitmap
                currentDrawableRes = null
                showLiveCamera = false
                viewModel.scanDish(bitmap, manualDishInput.ifBlank { null }, selectedMealType)
            },
            onNavigateBack = { showLiveCamera = false }
        )
        return
    }

    // System BackHandler
    BackHandler {
        onNavigateBack()
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            currentPreviewBitmap = bitmap
            currentDrawableRes = null
            viewModel.scanDish(bitmap, manualDishInput.ifBlank { null }, selectedMealType)
        }
    }

    // Photo Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    currentPreviewBitmap = bitmap
                    currentDrawableRes = null
                    viewModel.scanDish(bitmap, manualDishInput.ifBlank { null }, selectedMealType)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Reticle scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "scannerLaser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserOffset"
    )

    // Fallback Clarification Dialog
    if (scannerState.showClarificationDialog) {
        ClarificationDialog(
            dishName = scannerState.clarificationDishName,
            onDishNameChange = { viewModel.updateClarificationDishName(it) },
            ingredients = scannerState.clarificationIngredients,
            onIngredientsChange = { viewModel.updateClarificationIngredients(it) },
            calories = scannerState.clarificationCalories,
            onCaloriesChange = { viewModel.updateClarificationCalories(it) },
            mealType = scannerState.clarificationMealType,
            onMealTypeChange = { viewModel.updateClarificationMealType(it) },
            recognitionResult = scannerState.result,
            onSelectAlternative = { viewModel.applyAlternativeDish(it) },
            onConfirm = {
                viewModel.confirmAndLogScannedMeal()
                onNavigateBack()
            },
            onDismiss = { viewModel.dismissClarification() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F171A)) // Dark viewfinder background
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
            .testTag("scanner_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .testTag("scanner_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "AI Food Recognition",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Vibe AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Viewfinder Frame (Matching Mockup with Corner Reticle)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E282D)),
            contentAlignment = Alignment.Center
        ) {
            // Food image preview
            if (currentPreviewBitmap != null) {
                Image(
                    bitmap = currentPreviewBitmap!!.asImageBitmap(),
                    contentDescription = "Food Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (currentDrawableRes != null) {
                Image(
                    painter = painterResource(id = currentDrawableRes!!),
                    contentDescription = "Sample Food Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showLiveCamera = true }
                        .background(Color(0xFF162024)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Kamera öffnen oder Test-Gericht wählen",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tippe hier oder wähle unten eine Option",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Viewfinder Reticle Overlay (Square with rounded corners)
            Box(
                modifier = Modifier
                    .fillMaxSize(0.82f)
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                // Scanning Laser beam
                if (scannerState.isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .padding(top = (laserOffsetY * 220).dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.primary,
                                        Color.White,
                                        MaterialTheme.colorScheme.primary,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            // HUD Bottom Label
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (scannerState.isScanning) "Analyzing plate composition..." else "Recognize dish...",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Capture & Source Controls (Instant Camera, Gallery & One-Tap Test Dishes)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showLiveCamera = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("snap_photo_button")
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live Camera")
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.15f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_gallery_button")
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gallery")
                }
            }

            // Fast Test Sample Dishes (Multi-Cuisine)
            Text(
                text = "TEST-GERICHTE (ZUM SCHNELLEN TESTEN)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SamplePlatePill(
                    title = "🥗 Salat",
                    onClick = {
                        currentDrawableRes = R.drawable.sample_avocado_bowl_1790677164525
                        currentPreviewBitmap = null
                        manualDishInput = ""
                        val bm = BitmapFactory.decodeResource(context.resources, R.drawable.sample_avocado_bowl_1790677164525)
                        viewModel.scanDish(bm, "Lachs Avocado Quinoa Bowl", selectedMealType)
                    },
                    modifier = Modifier.weight(1f)
                )

                SamplePlatePill(
                    title = "🍕 Pizza",
                    onClick = {
                        currentDrawableRes = R.drawable.sample_pizza_margherita_1790679055472
                        currentPreviewBitmap = null
                        manualDishInput = ""
                        val bm = BitmapFactory.decodeResource(context.resources, R.drawable.sample_pizza_margherita_1790679055472)
                        viewModel.scanDish(bm, "Pizza Margherita", selectedMealType)
                    },
                    modifier = Modifier.weight(1f)
                )

                SamplePlatePill(
                    title = "🍣 Sushi",
                    onClick = {
                        currentDrawableRes = R.drawable.sample_sushi_plate_1790679068733
                        currentPreviewBitmap = null
                        manualDishInput = ""
                        val bm = BitmapFactory.decodeResource(context.resources, R.drawable.sample_sushi_plate_1790679068733)
                        viewModel.scanDish(bm, "Sushi Nigiri Set", selectedMealType)
                    },
                    modifier = Modifier.weight(1f)
                )

                SamplePlatePill(
                    title = "🌭 Wurst",
                    onClick = {
                        currentDrawableRes = R.drawable.sample_currywurst_1790677151376
                        currentPreviewBitmap = null
                        manualDishInput = ""
                        val bm = BitmapFactory.decodeResource(context.resources, R.drawable.sample_currywurst_1790677151376)
                        viewModel.scanDish(bm, "Berliner Currywurst mit Pommes", selectedMealType)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Optional Manual Query / Clarification Hint Input
            OutlinedTextField(
                value = manualDishInput,
                onValueChange = { manualDishInput = it },
                label = { Text("Gericht eingeben oder Spezifikation (optional)", color = Color.White.copy(alpha = 0.7f)) },
                placeholder = { Text("z.B. Spaghetti, Rindersteak, Döner, Müsli...") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val bm = currentPreviewBitmap ?: currentDrawableRes?.let {
                                BitmapFactory.decodeResource(context.resources, it)
                            }
                            viewModel.scanDish(
                                bm,
                                manualDishInput.ifBlank { null },
                                selectedMealType
                            )
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_dish_input")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Action Button: "Vibe Code: Generated" / "Analyze Vibe" (Matching screenshot!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    val bm = currentPreviewBitmap ?: currentDrawableRes?.let {
                        BitmapFactory.decodeResource(context.resources, it)
                    }
                    val hint = manualDishInput.ifBlank { null }
                    if (bm != null || hint != null) {
                        viewModel.scanDish(bm, hint, selectedMealType)
                    } else {
                        showLiveCamera = true
                    }
                },
                enabled = !scannerState.isScanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("analyze_vibe_button")
            ) {
                if (scannerState.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vibe Code: Analysieren...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentPreviewBitmap != null || currentDrawableRes != null || manualDishInput.isNotBlank()) "Vibe Code: Auswerten" else "Foto aufnehmen / Scannen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Recognition Result Card (When available)
        AnimatedVisibility(visible = scannerState.result != null) {
            val result = scannerState.result ?: return@AnimatedVisibility

            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("scanner_result_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result.dishName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = result.portion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CalorieAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${result.calories} kcal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CalorieAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Macro Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResultMacroChip("Protein", "${result.protein.toInt()}g", ProteinAccent, Modifier.weight(1f))
                        ResultMacroChip("Carbs", "${result.carbs.toInt()}g", CarbsAccent, Modifier.weight(1f))
                        ResultMacroChip("Fat", "${result.fat.toInt()}g", FatAccent, Modifier.weight(1f))
                        ResultMacroChip("Fiber", "${result.fiber.toInt()}g", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vibe Summary
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = result.vibeSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Detected Ingredients
                    if (result.detectedItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Detected: ${result.detectedItems.joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons: Confirm & Log, or Clarify/Edit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.confirmAndLogScannedMeal()
                                onNavigateBack()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("log_meal_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Meal", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.scanDish(
                                    currentPreviewBitmap,
                                    result.dishName,
                                    selectedMealType
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("edit_clarification_button")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clarify")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SamplePlatePill(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = modifier
            .clickable { onClick() }
            .testTag("sample_pill_$title")
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ResultMacroChip(
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
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

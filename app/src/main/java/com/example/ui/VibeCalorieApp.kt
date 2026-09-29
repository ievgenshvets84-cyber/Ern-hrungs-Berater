package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.RecipeBookScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.VibeViewModel

enum class VibeScreen(val title: String) {
    HOME("Dashboard"),
    SCANNER("AI Food Scanner"),
    RECIPES("Rezeptbuch"),
    INSIGHTS("Health & Tips"),
    SETTINGS("Targets & Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibeCalorieApp(
    viewModel: VibeViewModel = viewModel()
) {
    var currentScreen by rememberSaveable { mutableStateOf(VibeScreen.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            if (currentScreen != VibeScreen.SCANNER) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.vibe_calorie_icon_1790677051566),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vibe Calorie AI",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != VibeScreen.SCANNER) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == VibeScreen.HOME,
                        onClick = { currentScreen = VibeScreen.HOME },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == VibeScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Today", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_home_button")
                    )

                    NavigationBarItem(
                        selected = currentScreen == VibeScreen.SCANNER,
                        onClick = { currentScreen = VibeScreen.SCANNER },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == VibeScreen.SCANNER) Icons.Filled.CameraAlt else Icons.Outlined.CameraAlt,
                                contentDescription = "Scan Food"
                            )
                        },
                        label = { Text("AI Scan", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_scan_button")
                    )

                    NavigationBarItem(
                        selected = currentScreen == VibeScreen.RECIPES,
                        onClick = { currentScreen = VibeScreen.RECIPES },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == VibeScreen.RECIPES) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook,
                                contentDescription = "Rezeptbuch"
                            )
                        },
                        label = { Text("Rezepte", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_recipes_button")
                    )

                    NavigationBarItem(
                        selected = currentScreen == VibeScreen.INSIGHTS,
                        onClick = { currentScreen = VibeScreen.INSIGHTS },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == VibeScreen.INSIGHTS) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                                contentDescription = "Insights"
                            )
                        },
                        label = { Text("Insights", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_insights_button")
                    )

                    NavigationBarItem(
                        selected = currentScreen == VibeScreen.SETTINGS,
                        onClick = { currentScreen = VibeScreen.SETTINGS },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == VibeScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Ziele", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_settings_button")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == VibeScreen.HOME) {
                FloatingActionButton(
                    onClick = { currentScreen = VibeScreen.SCANNER },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.testTag("fab_scan_dish")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Scan Plate")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Scan Plate", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    VibeScreen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToScanner = { currentScreen = VibeScreen.SCANNER }
                    )
                    VibeScreen.SCANNER -> ScannerScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = VibeScreen.HOME }
                    )
                    VibeScreen.RECIPES -> RecipeBookScreen(
                        viewModel = viewModel
                    )
                    VibeScreen.INSIGHTS -> InsightsScreen(
                        viewModel = viewModel,
                        onNavigateToRecipes = { currentScreen = VibeScreen.RECIPES }
                    )
                    VibeScreen.SETTINGS -> SettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

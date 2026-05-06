package com.example.kreeda_prerana

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.kreeda_prerana.ui.screens.*
import com.example.kreeda_prerana.ui.theme.Kreeda_preranaTheme
import com.example.kreeda_prerana.ui.viewmodel.AthleteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kreeda_preranaTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    // Keep for compatibility
    val icon: ImageVector? = null
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, Icons.Filled.Dashboard)
    object AthleteList : Screen("athlete_list", "Athletes", Icons.Filled.Group, Icons.Outlined.Group, Icons.Filled.Group)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard, Icons.Filled.Leaderboard)
    object AddAthlete : Screen("add_athlete", "Add Athlete")
    object AthleteDetail : Screen("athlete_detail/{athleteId}", "Athlete Detail")
    object TrialLogger : Screen("trial_logger", "Trial Logger")
    object BatchEntry : Screen("batch_entry", "Batch Entry")
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    val application = LocalContext.current.applicationContext as Application
    val viewModel: AthleteViewModel = viewModel(
        factory = AndroidViewModelFactory.getInstance(application)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.AthleteList,
        Screen.Leaderboard
    )

    Scaffold(
        bottomBar = {
            if (bottomNavItems.any { it.route == currentDestination?.route }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    screen.title,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToAddAthlete = { navController.navigate(Screen.AddAthlete.route) },
                    onNavigateToAthleteList = { navController.navigate(Screen.AthleteList.route) },
                    onNavigateToTrialLogger = { navController.navigate(Screen.TrialLogger.route) },
                    onNavigateToBatchEntry = { navController.navigate(Screen.BatchEntry.route) }
                )
            }

            composable(Screen.AthleteList.route) {
                AthleteListScreen(
                    viewModel = viewModel,
                    onNavigateToAddAthlete = { navController.navigate(Screen.AddAthlete.route) },
                    onNavigateToAthleteDetail = { id ->
                        navController.navigate("athlete_detail/$id")
                    }
                )
            }

            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(viewModel = viewModel)
            }

            composable(Screen.AddAthlete.route) {
                AddEditAthleteScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AthleteDetail.route,
                arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("athleteId") ?: return@composable
                AthleteDetailScreen(
                    athleteId = id,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TrialLogger.route) {
                TrialLoggerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BatchEntry.route) {
                BatchEntryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
package com.aistudyhelper.navigation

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aistudyhelper.screens.AIChatScreen
import com.aistudyhelper.screens.FlashcardScreen
import com.aistudyhelper.screens.ResultsScreen
import com.aistudyhelper.screens.SubjectsScreen
import com.aistudyhelper.viewmodels.FlashcardViewModel

@Composable
fun AIStudyHelperApp() {
    val navController = rememberNavController()
    val bottomScreens = listOf(Screen.AIChat, Screen.Subjects, Screen.Results)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomScreens.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigation(
                    screens = bottomScreens,
                    currentRoute = currentRoute,
                    onScreenClick = { screen ->
                        navigateTopLevelDestination(navController, screen.route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.AIChat.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.AIChat.route) {
                AIChatScreen()
            }
            composable(Screen.Subjects.route) {
                SubjectsScreen(
                    onSubjectClick = { subject ->
                        navController.navigate("${Screen.Flashcards.route}/${Uri.encode(subject)}")
                    }
                )
            }
            composable(Screen.Results.route) {
                ResultsScreen()
            }
            composable(
                route = "${Screen.Flashcards.route}/{subject}",
                arguments = listOf(navArgument("subject") { type = NavType.StringType })
            ) { entry ->
                val application = LocalContext.current.applicationContext as Application
                val subject = Uri.decode(entry.arguments?.getString("subject").orEmpty())
                val viewModel: FlashcardViewModel = viewModel(
                    key = "flashcards_$subject",
                    factory = FlashcardViewModel.factory(application, subject)
                )

                FlashcardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack(Screen.Subjects.route, inclusive = false) },
                    onResultsClick = {
                        navController.navigate(Screen.Results.route) {
                            popUpTo(Screen.Subjects.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigation(
    screens: List<Screen>,
    currentRoute: String?,
    onScreenClick: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                NavPill(
                    title = screen.title,
                    icon = screen.icon,
                    selected = currentRoute == screen.route,
                    onClick = { onScreenClick(screen) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavPill(
    title: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                modifier = Modifier.padding(start = 7.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

private fun navigateTopLevelDestination(navController: NavController, route: String) {
    if (!navController.popBackStack(route, inclusive = false)) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
        }
    }
}

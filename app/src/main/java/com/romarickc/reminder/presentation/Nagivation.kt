package com.romarickc.reminder.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.romarickc.reminder.presentation.screen.hTips.HydrationTipsScreen
import com.romarickc.reminder.presentation.screen.home.HomeScreen
import com.romarickc.reminder.presentation.screen.intakeH.IntakeHistoryScreen
import com.romarickc.reminder.presentation.screen.intakeTarget.IntakeTargetScreen
import com.romarickc.reminder.presentation.screen.notifSettings.NotifSettingsScreen
import com.romarickc.reminder.presentation.screen.rIntake.RegisterIntakeScreen
import com.romarickc.reminder.presentation.screen.importExport.ImportExportScreen
import com.romarickc.reminder.presentation.utils.Routes

@Composable
fun NavigationM() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable(Routes.Home) {
            HomeScreen(onNavigate = { destination ->
                navController.navigate(destination.route)
            })
        }
        composable(Routes.RegIntake) {
            RegisterIntakeScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.IntakeHistory) {
            IntakeHistoryScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.HydrationTips) {
            HydrationTipsScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.SetTarget) {
            IntakeTargetScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.NotifSettings) {
            NotifSettingsScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.ImportExport) {
            ImportExportScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
    }
}
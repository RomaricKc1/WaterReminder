package com.romarickc.reminder.presentation

import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.romarickc.reminder.presentation.screen.hTips.HydrationTipsScreen
import com.romarickc.reminder.presentation.screen.home.HomeScreen
import com.romarickc.reminder.presentation.screen.importExport.ImportExportScreen
import com.romarickc.reminder.presentation.screen.intakeH.IntakeHistoryScreen
import com.romarickc.reminder.presentation.screen.intakeTarget.IntakeTargetScreen
import com.romarickc.reminder.presentation.screen.notifSettings.NotifSettingsScreen
import com.romarickc.reminder.presentation.screen.rIntake.RegisterIntakeScreen
import com.romarickc.reminder.presentation.utils.Routes

@Suppress("ktlint:standard:function-naming")
@Composable
fun NavigationM() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(onNavigate = { destination ->
                navController.navigate(destination.route)
            })
        }
        composable(Routes.REGISTER_INTAKE) {
            RegisterIntakeScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.INTAKE_HISTORY) {
            IntakeHistoryScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.HYDRATION_TIPS) {
            HydrationTipsScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.SET_TARGET) {
            IntakeTargetScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.NOTIF_SETTINGS) {
            NotifSettingsScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.IMPORT_EXPORT) {
            ImportExportScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
    }
}

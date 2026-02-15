package com.romarickc.reminder.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.romarickc.reminder.commons.Routes
import com.romarickc.reminder.presentation.screens.about.AboutScreen
import com.romarickc.reminder.presentation.screens.home.HomeScreen
import com.romarickc.reminder.presentation.screens.hydrationTips.HydrationTipsScreen
import com.romarickc.reminder.presentation.screens.importExport.ImportExportScreen
import com.romarickc.reminder.presentation.screens.importExport.ServerScreen
import com.romarickc.reminder.presentation.screens.intakeHistory.DaysGraphScreen
import com.romarickc.reminder.presentation.screens.intakeHistory.IntakeHistoryScreen
import com.romarickc.reminder.presentation.screens.intakeHistory.MonthsGraphScreen
import com.romarickc.reminder.presentation.screens.intakeTarget.IntakeTargetScreen
import com.romarickc.reminder.presentation.screens.registerIntake.RegisterIntakeScreen
import com.romarickc.reminder.presentation.screens.settings.LanguageSettingsScreen
import com.romarickc.reminder.presentation.screens.settings.NotifSettingsScreen
import com.romarickc.reminder.presentation.screens.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        composable(Routes.INTAKE_HISTORY) {
            IntakeHistoryScreen(onNavigate = { destination ->
                navController.navigate(destination.route)
            })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onNavigate = { destination ->
                navController.navigate(destination.route)
            })
        }
        composable(Routes.IMPORT_EXPORT) {
            ImportExportScreen(onNavigate = { destination ->
                navController.navigate(destination.route)
            })
        }

        // /////////////////////////////////////////////////////////////////////////////////////////
        composable(Routes.REGISTER_INTAKE) {
            RegisterIntakeScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.SEE_DAYS_INTAKE_GRAPH) {
            DaysGraphScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.SEE_MONTHS_INTAKE_GRAPH) {
            MonthsGraphScreen(onPopBackStack = {
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
        composable(Routes.LANG_SETTINGS) {
            LanguageSettingsScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.ABOUT) {
            AboutScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
        composable(Routes.SERVER) {
            ServerScreen(onPopBackStack = {
                navController.popBackStack()
            })
        }
    }
}

package com.romarickc.reminder.presentation.screen.home

sealed class HomeScreenEvents {
    object OnAddNewIntakeClick : HomeScreenEvents()
    object OnIntakeHistory : HomeScreenEvents()
    object OnHydraTips : HomeScreenEvents()
    object OnSetTarget : HomeScreenEvents()
    object OnNotifSettings : HomeScreenEvents()
    object OnImportExportData : HomeScreenEvents()
}
package com.romarickc.reminder.presentation.screens.home

sealed class HomeScreenEvents {
    object OnAddNewIntakeClick : HomeScreenEvents()

    object OnIntakeHistory : HomeScreenEvents()

    object OnHydraTips : HomeScreenEvents()

    object OnSetTarget : HomeScreenEvents()

    object OnSettings : HomeScreenEvents()

    object OnImportExportData : HomeScreenEvents()

    object OnAbout : HomeScreenEvents()
}

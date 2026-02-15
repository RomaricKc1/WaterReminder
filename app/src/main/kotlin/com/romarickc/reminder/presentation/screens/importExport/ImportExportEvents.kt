package com.romarickc.reminder.presentation.screens.importExport

sealed class ImportExportEvents {
    data class OnImportClick(
        val q: Int,
    ) : ImportExportEvents()

    data class OnExportClick(
        val q: Int,
    ) : ImportExportEvents()

    data class OnChgServerAddress(
        val q: String,
    ) : ImportExportEvents()

    object OnExportToServer : ImportExportEvents()

    object OnSeeServerPageClick : ImportExportEvents()

    object OnLoadServerIntakeData : ImportExportEvents()

    object OnServerPing : ImportExportEvents()
}

package com.romarickc.reminder.presentation.screen.importExport

sealed class ImportExportEvents {
    data class OnImportclick(val q: Int) : ImportExportEvents()
    data class OnExportclick(val q: Int) : ImportExportEvents()
}
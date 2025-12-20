package com.romarickc.reminder.presentation.screens.importExport

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.getFileToImport
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ImportExportViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
        private val application: Application,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        @OptIn(DelicateCoroutinesApi::class)
        fun onEvent(event: ImportExportEvents) {
            when (event) {
                // export
                is ImportExportEvents.OnExportClick -> {
                    onExport()
                }

                // import
                is ImportExportEvents.OnImportClick -> {
                    var done = -1
                    val importJob =
                        GlobalScope.launch {
                            try {
                                val pickedFile =
                                    getFileToImport(application.filesDir.path)
                                if (repository.importFromFile(pickedFile.path) == 0) {
                                    done = 1
                                    Log.i(
                                        "import_export",
                                        "imported from ${pickedFile.path}",
                                    )
                                } else {
                                    done = 0
                                    Log.i("import_export", "import error")
                                }
                            } catch (e: Exception) {
                                Log.e("import-e", "$e")
                                // e.printStackTrace()
                            }
                        }

                    viewModelScope.launch {
                        importJob.join()
                        onImport(done)
                    }
                }
            }
        }

        private fun onExport() {
            viewModelScope.launch {
                var text: String
                val thePath = application.filesDir.path
                val filename =
                    thePath + "/" +
                        "com.romarickc.reminder_${Instant.now().toEpochMilli()}.dat"
                if (repository.getAllAndExportToFile(filename) == 0) {
                    text =
                        application.applicationContext
                            .getString(R.string.export_success)
                    Log.i("import_export", "exported to $filename")
                } else {
                    text =
                        application.applicationContext
                            .getString(R.string.export_failure)
                    Log.i("import_export", "export error")
                }
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(application, text, duration)
                toast.show()
            }
        }

        private fun onImport(done: Int) {
            viewModelScope.launch {
                val text =
                    if (done == 1) {
                        application.applicationContext
                            .getString(R.string.import_success)
                    } else {
                        application.applicationContext
                            .getString(R.string.import_failure)
                    }
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(application, text, duration)
                toast.show()
            }
        }
    }

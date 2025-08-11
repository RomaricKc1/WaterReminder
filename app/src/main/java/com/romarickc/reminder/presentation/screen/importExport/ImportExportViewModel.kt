package com.romarickc.reminder.presentation.screen.importExport

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
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
                is ImportExportEvents.OnExportclick -> {
                    viewModelScope.launch {
                        var text: String
                        val thepath = application.filesDir.path
                        val filename =
                            thepath + "/" + "com.romarickc.reminder_${Instant.now().toEpochMilli()}.dat"
                        if (repository.getAllAndExportToFile(filename) == 0) {
                            text = "Export successful"
                            Log.i("import_export", "exported to $filename")
                        } else {
                            text = "Export error"
                            Log.i("import_export", "export error")
                        }
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(application, text, duration)
                        toast.show()
                    }
                }

                // import
                is ImportExportEvents.OnImportclick -> {
                    var done = 0
                    val importjob =
                        GlobalScope.launch {
                            val filename = "com.romarickc.reminder_"

                            val thepath = application.filesDir.path
                            val availableForImport = mutableListOf<File>()

                            try {
                                File(thepath).walk().forEach {
                                    if (it.path.contains(filename)) {
                                        availableForImport += it
                                    }
                                    Log.i("files found", "$it, ${it.path}")
                                }
                                // Log.i("files avail", "$availableForImport")
                                // get the latest exported data
                                var pickedFile = availableForImport[0]
                                for (pathfound in availableForImport) {
                                    val pathtimestamp = pathfound.path.split("_")[1].split(".")[0]
                                    val pickedFileTimestamp = pickedFile.path.split("_")[1].split(".")[0]

                                    if (pickedFileTimestamp.toLong() < pathtimestamp.toLong()) {
                                        pickedFile = pathfound
                                    }
                                    Log.i("files found", "${pathfound.path} --> $pathtimestamp")
                                }
                                if (repository.importFromFile(pickedFile.path) == 0) {
                                    done = 1
                                    Log.i("import_export", "imported from ${pickedFile.path}")
                                } else {
                                    Log.i("import_export", "import error")
                                }
                            } catch (e: Exception) {
                                Log.e("import-e", "$e")
                                // e.printStackTrace()
                            }
                        }
                    viewModelScope.launch {
                        importjob.join()
                        val text =
                            if (done == 1) {
                                "Import successful"
                            } else {
                                "Import error"
                            }
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(application, text, duration)
                        toast.show()
                    }
                }
            }
        }
    }

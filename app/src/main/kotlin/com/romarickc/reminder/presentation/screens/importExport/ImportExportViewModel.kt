package com.romarickc.reminder.presentation.screens.importExport

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.AnyState
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.E_ImportServerError
import com.romarickc.reminder.commons.ExportIntakesStream
import com.romarickc.reminder.commons.QrGenerator
import com.romarickc.reminder.commons.Routes
import com.romarickc.reminder.commons.ServerPing
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.UiEvent.Navigate
import com.romarickc.reminder.commons.exportToSever
import com.romarickc.reminder.commons.getFileToImport
import com.romarickc.reminder.commons.importHelper
import com.romarickc.reminder.commons.loadServerAddress
import com.romarickc.reminder.commons.readTypeCompanion
import com.romarickc.reminder.commons.updateServerAddress
import com.romarickc.reminder.domain.repository.CommRepository
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ImportExportViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
        private val commRepository: CommRepository,
        private val application: Application,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
        val qrBitmap: StateFlow<Bitmap?> = _qrBitmap.asStateFlow()

        var serverDataState by mutableStateOf(AnyState<ExportIntakesStream>())
            private set

        var serverPingState by mutableStateOf(AnyState<ServerPing>())
            private set

        var storedSAddress by mutableStateOf(Constants.SERVER_ADDR)
            private set

        init {
            serverDataState = serverDataState.copy(loading = false, error = null)
            serverPingState = serverPingState.copy(loading = false, error = null)
            storedSAddress = loadServerAddress(application)
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun onEvent(event: ImportExportEvents) {
            when (event) {
                // export
                is ImportExportEvents.OnExportClick -> {
                    onExport()
                }

                // import
                is ImportExportEvents.OnImportClick -> {
                    onImport()
                }

                is ImportExportEvents.OnSeeServerPageClick -> {
                    emitEvent(
                        Navigate(
                            route = Routes.SERVER,
                        ),
                    )
                }

                is ImportExportEvents.OnChgServerAddress -> {
                    updateServerAddress(event.q, application)
                    updateUiAddress(event.q)
                }

                is ImportExportEvents.OnLoadServerIntakeData -> {
                    var importStatus = E_ImportServerError.INIT
                    val importJob =
                        GlobalScope.launch {
                            importStatus = importServerData()
                        }
                    viewModelScope.launch {
                        importJob.join()
                        importHelper(application, importStatus)
                    }
                }

                is ImportExportEvents.OnServerPing -> {
                    getServerPong()
                }

                is ImportExportEvents.OnExportToServer -> {
                    viewModelScope.launch {
                        val resCompressed = exportToSever(application, repository, commRepository)
                        generateQrCode(resCompressed)
                    }
                }
            }
        }

        fun updateUiAddress(address: String) {
            storedSAddress = address
        }

        fun generateQrCode(resCompressed: ByteArray?) {
            var thisContent = Constants.DEFAULT_QR_CODE_CONTENT
            if (resCompressed != null) {
                if (resCompressed.size > Constants.MAX_QR_CODE_BYTES) {
                    thisContent =
                        String(
                            resCompressed
                                .slice(0..Constants.MAX_QR_CODE_BYTES)
                                .toByteArray(),
                        )
                    val duration = Toast.LENGTH_SHORT
                    val toast: Toast =
                        Toast.makeText(
                            application,
                            application.applicationContext
                                .getString(R.string.qr_code_error_large)
                                .format(Constants.MAX_QR_CODE_BYTES),
                            duration,
                        )

                    toast.show()
                } else {
                    thisContent = String(resCompressed)
                }
            }
            _qrBitmap.value =
                QrGenerator
                    .encodeAsBitmap(
                        thisContent,
                        Constants.QR_CODE_1L,
                        Constants.QR_CODE_1L,
                    )!!
        }

        fun getServerPong() {
            viewModelScope.launch {
                serverPingState =
                    readTypeCompanion(
                        serverPingState,
                        commRepository.serverPing(),
                    )
                serverPingState.data?.let {
                    val duration = Toast.LENGTH_SHORT
                    val toast: Toast =
                        Toast.makeText(
                            application,
                            application.applicationContext
                                .getString(R.string.pong),
                            duration,
                        )

                    toast.show()
                }
            }
        }

        suspend fun importServerData(): E_ImportServerError {
            serverDataState =
                readTypeCompanion(
                    serverDataState,
                    commRepository.getIntakesServer(),
                )

            serverDataState.data?.let { stream ->
                repository.importFromStr(stream.line)
                    ?: return E_ImportServerError.CONV_STR_DATA_ERROR

                return E_ImportServerError.SUCCESS
            }

            return E_ImportServerError.OTHER_ERROR
        }

        private fun emitEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.emit(event)
            }
        }

        @Deprecated("Using file system. I don't like it anymore. Use http server comm")
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

        @OptIn(DelicateCoroutinesApi::class)
        @Deprecated("Using file system. I don't like it anymore. Use http server comm")
        private fun onImport() {
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

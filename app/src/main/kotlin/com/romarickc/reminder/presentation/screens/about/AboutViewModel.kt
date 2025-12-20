package com.romarickc.reminder.presentation.screens.about

import androidx.lifecycle.ViewModel
import com.romarickc.reminder.BuildConfig
import com.romarickc.reminder.commons.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AboutViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME

        fun onEvent() {
        }
    }

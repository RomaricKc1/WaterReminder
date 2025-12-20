package com.romarickc.reminder.presentation.screens.hydrationTips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HydraTipsViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        fun onEvent() {
        }

        private fun emitEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.emit(event)
            }
        }
    }

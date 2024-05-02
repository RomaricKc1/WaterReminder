package com.romarickc.reminder.presentation.screen.intakeTarget

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.domain.model.IntakeTarget
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.utils.Constants
import com.romarickc.reminder.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntakeTargetViewModel @Inject constructor(
    val repository: WaterIntakeRepository
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var currentTarget = repository.getTarget(1)

    init {
        viewModelScope.launch {
            repository.insertTarget(Constants.RECOMMENDED_INTAKE)
        }
    }

    fun onEvent(event: IntakeTargetEvents) {
        when (event) {
            is IntakeTargetEvents.OnValueChange -> {
                viewModelScope.launch {
                    repository.updateTarget(IntakeTarget(1, event.q))
                    Log.i("targetdb", "updated")
                    _uiEvent.emit(UiEvent.PopBackStack)
                }
            }
        }
    }
}
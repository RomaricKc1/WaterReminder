package com.romarickc.reminder.presentation.screens.settings

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romarickc.reminder.commons.Constants.DB_NOTIF_LEVEL_IDX
import com.romarickc.reminder.commons.Constants.DEF_LANG
import com.romarickc.reminder.commons.Constants.LANG_EN
import com.romarickc.reminder.commons.Constants.LANG_FR
import com.romarickc.reminder.commons.Constants.LANG_KEY
import com.romarickc.reminder.commons.Constants.SHARED_DATA
import com.romarickc.reminder.commons.E_Languages
import com.romarickc.reminder.commons.Routes
import com.romarickc.reminder.commons.UiEvent
import com.romarickc.reminder.commons.UiEvent.Navigate
import com.romarickc.reminder.commons.loadSharedPref
import com.romarickc.reminder.commons.reSchedPeriodicWork
import com.romarickc.reminder.commons.storeSharedPref
import com.romarickc.reminder.domain.model.Preferences
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: WaterIntakeRepository,
        private val application: Application,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent = _uiEvent.asSharedFlow()

        var currentNotifPref = repository.getNotifPref(DB_NOTIF_LEVEL_IDX)

        var tmp =
            loadSharedPref(
                SHARED_DATA,
                LANG_KEY,
                DEF_LANG,
                application.applicationContext,
            )

        var currentLangPref =
            when (tmp) {
                LANG_FR -> {
                    E_Languages.FRANCAIS
                }
                LANG_EN -> {
                    E_Languages.ENGLISH
                }
                else -> E_Languages.FRANCAIS
            }

        init {
            viewModelScope.launch {
                repository.insertNotifPref(0)
            }
        }

        fun onEvent(event: SettingsEvents) {
            when (event) {
                is SettingsEvents.OnNotifValueChange -> {
                    onNotifUpdate(event.q)
                }

                is SettingsEvents.OnLanguageChange -> {
                    updateAppLanguage(
                        E_Languages.fromValue(event.q),
                        application.applicationContext,
                    )
                    viewModelScope.launch {
                        _uiEvent.emit(UiEvent.PopBackStack)
                    }
                }

                is SettingsEvents.OnLanguageSettingsClick -> {
                    emitEvent(
                        Navigate(
                            route = Routes.LANG_SETTINGS,
                        ),
                    )
                }

                is SettingsEvents.OnNotifSettingsClick -> {
                    emitEvent(
                        Navigate(
                            route = Routes.NOTIF_SETTINGS,
                        ),
                    )
                }
            }
        }

        private fun emitEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.emit(event)
            }
        }

        private fun onNotifUpdate(query: Int) {
            viewModelScope.launch {
                repository.updateNotifPref(
                    Preferences(DB_NOTIF_LEVEL_IDX, query),
                )
                reSchedPeriodicWork(
                    context = application,
                    notifPref = query,
                    careAboutDisabled = true,
                )
                // Log.i("settings", "notification pref updated ${event.q}")
                _uiEvent.emit(UiEvent.PopBackStack)
            }
        }

        private fun updateAppLanguage(
            targetLang: E_Languages,
            context: Context,
        ) {
            val newLang =
                when (targetLang) {
                    E_Languages.FRANCAIS -> {
                        LANG_FR
                    }

                    E_Languages.ENGLISH -> {
                        LANG_EN
                    }
                }
            storeSharedPref(
                SHARED_DATA,
                LANG_KEY,
                newLang,
                context,
            )
            update(context)
        }

        fun update(context: Context) {
            val refresh =
                Intent.makeRestartActivityTask(
                    ComponentName(
                        context,
                        MainActivity::class.java,
                    ),
                )
            context.startActivity(refresh)
        }
    }

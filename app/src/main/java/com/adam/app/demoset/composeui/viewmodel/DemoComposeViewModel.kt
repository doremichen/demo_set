/**
 * Copyright (C) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is a demo compose ui view model.
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2026/03/18
 */
package com.adam.app.demoset.composeui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.adam.app.demoset.R
import com.adam.app.demoset.composeui.model.DemoUiState
import com.adam.app.demoset.composeui.model.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DemoComposeViewModel : ViewModel() {
    // state flow: ui state
    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState: StateFlow<DemoUiState> = _uiState

    fun startService() {
        update { state -> state.copy(isServiceRunning = true) }
        addLog(R.string.demo_compose_log_start)
    }

    fun stopService() {
        update { state -> state.copy(isServiceRunning = false) }
        addLog(R.string.demo_compose_log_stop)
    }

    fun bindService() {
        update { state -> state.copy(isBound = true) }
        addLog(R.string.demo_compose_log_bind)
    }

    fun unbindService() {
        update { state -> state.copy(isBound = false) }
        addLog(R.string.demo_compose_log_unbind)
    }

    /**
     * add log
     */
    private fun addLog(@StringRes resId: Int) {
        update { state -> state.copy(logs = listOf(
            LogEntry(
                resId = resId
            )
        ) + state.logs) }
    }

    /**
     * update service running state
     */
    private fun update(block: (DemoUiState) -> DemoUiState) {
        _uiState.value = block(_uiState.value)
    }


}
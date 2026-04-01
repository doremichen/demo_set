/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
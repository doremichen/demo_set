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

package com.adam.app.demoset.flowlab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for FlowLab, demonstrating the usage of StateFlow and SharedFlow.
 */
class FlowLabViewModel : ViewModel() {

    // --- StateFlow: Persistent state used to drive UI (e.g., counters) ---
    private val _counterState = MutableStateFlow(0)
    val counterState: StateFlow<Int> = _counterState.asStateFlow()

    // --- SharedFlow: Hot stream for one-time events (e.g., Toast notifications) ---
    private val _toastEvent = MutableSharedFlow<Long>() // Emits current timestamp
    val toastEvent: SharedFlow<Long> = _toastEvent.asSharedFlow()

    // --- StateFlow: Log list for the console-like UI ---
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    /**
     * Adds a new log entry with a timestamp.
     */
    fun addLog(log: String) {
        val currentLogs = _logs.value.toMutableList()
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        currentLogs.add("[$timestamp] $log")
        _logs.value = currentLogs
    }

    /**
     * Increments the internal counter state.
     */
    fun incrementCounter() {
        _counterState.value += 1
    }

    /**
     * Triggers a one-time event via SharedFlow.
     */
    fun triggerEvent() {
        viewModelScope.launch {
            _toastEvent.emit(System.currentTimeMillis())
        }
    }

    /**
     * Clears all log entries.
     */
    fun clearLog() {
        _logs.value = emptyList()
    }
}

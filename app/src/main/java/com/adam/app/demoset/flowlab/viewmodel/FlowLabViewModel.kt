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

class FlowLabViewModel : ViewModel() {
    // --- StateFlow: used to control UI state ---
    private val _counterState = MutableStateFlow(0)
    val counterState: StateFlow<Int> = _counterState.asStateFlow()

    // --- ShareFlow: Used to single event ---
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // --- live data: log ---
    private val _logs = MutableStateFlow<List<String>>(ArrayList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()


    /**
     * add log
     */
    fun addLog(log: String) {
        val logs = _logs.value?.toMutableList() ?: ArrayList()
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        logs.add("\n[$timestamp] $log")
        _logs.value = logs
    }

    /**
     * increment counter
     */
    fun incrementCounter() {
        addLog("increment counter")
        _counterState.value = _counterState.value + 1
    }

    /**
     * trigger toast event
     */
    fun triggerEvent() {
        addLog("trigger toast event")
        viewModelScope.launch {
            _toastEvent.emit("來自 SharedFlow 的即時訊息！時間：${System.currentTimeMillis()}")
        }
    }

    /**
     * clear log
     */
    fun clearLog() {
        addLog("clear log")
        _logs.value = ArrayList()
    }
}
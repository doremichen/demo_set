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
package com.adam.app.demoset.performance.presentation.leak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.performance.domain.model.LeakReport
import com.adam.app.demoset.performance.domain.model.LeakStatus
import com.adam.app.demoset.performance.domain.usecases.ClearLeakedReferencesUseCase
import com.adam.app.demoset.performance.domain.usecases.MonitorLeakUseCase
import com.adam.app.demoset.performance.domain.usecases.MonitorStatusUseCase
import com.adam.app.demoset.performance.domain.usecases.SimulateMemoryLeakUseCase
import com.adam.app.demoset.performance.domain.usecases.WatchInstanceUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeakUiState(
    val status: LeakStatus = LeakStatus.ALIVE,
    val reports: List<LeakReport> = emptyList(),
    val isAnalyzing: Boolean = false,
)

class LeakViewModel(
    private val simulateMemoryLeakUseCase: SimulateMemoryLeakUseCase,
    private val monitorLeakUseCase: MonitorLeakUseCase,
    private val watchInstanceUseCase: WatchInstanceUseCase,
    private val monitorStatusUseCase: MonitorStatusUseCase,
    private val clearLeakedReferencesUseCase: ClearLeakedReferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeakUiState())
    val uiState: StateFlow<LeakUiState> = _uiState.asStateFlow()

    private val _navigateToMainEvent = MutableSharedFlow<Unit>()
    val navigateToMainEvent: SharedFlow<Unit> = _navigateToMainEvent.asSharedFlow()

    init {
        // Monitor detailed reports
        viewModelScope.launch {
            monitorLeakUseCase().collectLatest { report ->
                _uiState.update { currentState ->
                    currentState.copy(
                        status = report.status,
                        reports = currentState.reports + report,
                        isAnalyzing = false
                    )
                }
            }
        }

        // Monitor status updates (real callback mechanism)
        viewModelScope.launch {
            monitorStatusUseCase().collectLatest { status ->
                _uiState.update { currentState ->
                    currentState.copy(
                        status = status,
                        // If status is DESTROYED or LEAKED, analysis is complete, close the dialog
                        isAnalyzing = status == LeakStatus.ALIVE
                    )
                }
            }
        }
    }

    fun triggerLeak(instance: Any) {
        // Reset UI state to avoid leftovers from previous results
        _uiState.update { it.copy(status = LeakStatus.ALIVE, isAnalyzing = true) }
        simulateMemoryLeakUseCase(instance)
    }

    fun watchInstance(instance: Any, description: String) {
        // Reset UI state to avoid leftovers from previous results
        _uiState.update { it.copy(status = LeakStatus.ALIVE, isAnalyzing = true) }
        watchInstanceUseCase(instance, description)
    }

    fun clearLeakedReferences() {
        clearLeakedReferencesUseCase()
    }

    fun clearReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(reports = emptyList(), status = LeakStatus.ALIVE) }
        }
    }
}

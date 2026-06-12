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

package com.adam.app.demoset.performance.presentation.benchmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.performance.domain.model.BenchmarkReport
import com.adam.app.demoset.performance.domain.usecases.ManageUiBenchmarkUseCase
import com.adam.app.demoset.performance.domain.usecases.RunAlgorithmBenchmarkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BenchmarkUiState(
    val isRunning: Boolean = false,
    val algorithmReport: BenchmarkReport? = null,
    val uiReport: BenchmarkReport? = null
)

class BenchmarkViewModel(
    private val runAlgorithmBenchmarkUseCase: RunAlgorithmBenchmarkUseCase,
    private val manageUiBenchmarkUseCase: ManageUiBenchmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BenchmarkUiState())
    val uiState: StateFlow<BenchmarkUiState> = _uiState.asStateFlow()

    fun runAlgorithmComparison() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true)
            
            // simulate Bubble Sort vs Quick Sort
            val report = runAlgorithmBenchmarkUseCase("Algorithm Comparison") {
                // simple simulated delay to represent work
                Thread.sleep(100)
            }
            
            _uiState.value = _uiState.value.copy(isRunning = false, algorithmReport = report)
        }
    }

    fun startUiBenchmark() {
        manageUiBenchmarkUseCase.startSession()
        _uiState.value = _uiState.value.copy(isRunning = true)
    }

    fun stopUiBenchmark() {
        val report = manageUiBenchmarkUseCase.stopSession("UI Scrolling")
        _uiState.value = _uiState.value.copy(isRunning = false, uiReport = report)
    }
}

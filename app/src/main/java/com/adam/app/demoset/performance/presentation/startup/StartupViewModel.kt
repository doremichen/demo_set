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

package com.adam.app.demoset.performance.presentation.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.performance.domain.model.ExecutionResult
import com.adam.app.demoset.performance.domain.usecases.ExecuteStartupUseCase
import com.adam.app.demoset.performance.framework.startup.AsyncStartupStrategy
import com.adam.app.demoset.performance.framework.startup.SyncBlockingStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StartupUiState(
    val isLoading: Boolean = false,
    val result: ExecutionResult? = null,
    val errorMessage: String? = null
)

class StartupViewModel(
    private val executeStartupUseCase: ExecuteStartupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartupUiState())
    val uiState: StateFlow<StartupUiState> = _uiState.asStateFlow()

    fun runSyncStartup() {
        runStartup(SyncBlockingStrategy())
    }

    fun runAsyncStartup() {
        runStartup(AsyncStartupStrategy())
    }

    private fun runStartup(strategy: com.adam.app.demoset.performance.framework.startup.StartupStrategy) {
        viewModelScope.launch {
            _uiState.value = StartupUiState(isLoading = true)
            try {
                val result = executeStartupUseCase(strategy)
                _uiState.value = StartupUiState(result = result)
            } catch (e: Exception) {
                _uiState.value = StartupUiState(errorMessage = e.message)
            }
        }
    }
}

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

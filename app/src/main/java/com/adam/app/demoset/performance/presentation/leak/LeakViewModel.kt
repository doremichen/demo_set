package com.adam.app.demoset.performance.presentation.leak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.performance.domain.model.LeakReport
import com.adam.app.demoset.performance.domain.model.LeakStatus
import com.adam.app.demoset.performance.domain.usecases.MonitorLeakUseCase
import com.adam.app.demoset.performance.domain.usecases.SimulateMemoryLeakUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LeakUiState(
    val status: LeakStatus = LeakStatus.ALIVE,
    val reports: List<LeakReport> = emptyList()
)

class LeakViewModel(
    private val simulateMemoryLeakUseCase: SimulateMemoryLeakUseCase,
    private val monitorLeakUseCase: MonitorLeakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeakUiState())
    val uiState: StateFlow<LeakUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            monitorLeakUseCase().collectLatest { report ->
                _uiState.value = _uiState.value.copy(
                    status = report.status,
                    reports = _uiState.value.reports + report
                )
            }
        }
    }

    fun triggerLeak(instance: Any) {
        val newStatus = simulateMemoryLeakUseCase(instance)
        _uiState.value = _uiState.value.copy(status = newStatus)
    }
}

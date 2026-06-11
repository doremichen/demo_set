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

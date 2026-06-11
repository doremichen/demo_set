/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.performance.presentation.leak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adam.app.demoset.performance.domain.usecases.MonitorLeakUseCase
import com.adam.app.demoset.performance.domain.usecases.SimulateMemoryLeakUseCase
import com.adam.app.demoset.performance.framework.LeakCanary.LeakCanaryBridge

class LeakDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val leakRepo = LeakCanaryBridge()
        val simulateLeakUseCase = SimulateMemoryLeakUseCase(leakRepo)
        val monitorLeakUseCase = MonitorLeakUseCase(leakRepo)

        setContent {
            val viewModel: LeakViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return LeakViewModel(simulateLeakUseCase, monitorLeakUseCase) as T
                }
            })
            LeakScreen(viewModel = viewModel, onBack = { finish() })
        }
    }
}

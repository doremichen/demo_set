/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.performance.presentation.benchmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adam.app.demoset.performance.domain.usecases.ManageUiBenchmarkUseCase
import com.adam.app.demoset.performance.domain.usecases.RunAlgorithmBenchmarkUseCase
import com.adam.app.demoset.performance.framework.benchmark.BenchmarkRepositoryImpl

class BenchmarkDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val benchmarkRepo = BenchmarkRepositoryImpl()
        val runAlgoBenchmarkUseCase = RunAlgorithmBenchmarkUseCase(benchmarkRepo)
        val manageUiBenchmarkUseCase = ManageUiBenchmarkUseCase(benchmarkRepo)

        setContent {
            val viewModel: BenchmarkViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return BenchmarkViewModel(runAlgoBenchmarkUseCase, manageUiBenchmarkUseCase) as T
                }
            })
            BenchmarkScreen(viewModel = viewModel, onBack = { finish() })
        }
    }
}

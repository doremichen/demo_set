/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.performance.presentation.startup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adam.app.demoset.performance.domain.usecases.ExecuteStartupUseCase
import com.adam.app.demoset.performance.framework.startup.StartupRepositoryImpl

class StartupDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val startupRepo = StartupRepositoryImpl()
        val startupUseCase = ExecuteStartupUseCase(startupRepo)

        setContent {
            val viewModel: StartupViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return StartupViewModel(startupUseCase) as T
                }
            })
            StartupScreen(viewModel = viewModel, onBack = { finish() })
        }
    }
}

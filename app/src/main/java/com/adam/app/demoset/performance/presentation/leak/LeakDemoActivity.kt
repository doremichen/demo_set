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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adam.app.demoset.performance.domain.usecases.ClearLeakedReferencesUseCase
import com.adam.app.demoset.performance.domain.usecases.MonitorLeakUseCase
import com.adam.app.demoset.performance.domain.usecases.MonitorStatusUseCase
import com.adam.app.demoset.performance.domain.usecases.SimulateMemoryLeakUseCase
import com.adam.app.demoset.performance.domain.usecases.WatchInstanceUseCase
import com.adam.app.demoset.performance.framework.LeakCanary.LeakCanaryBridge

class LeakDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val leakRepo by lazy { LeakCanaryBridge() }
        val simulateLeakUseCase by lazy { SimulateMemoryLeakUseCase(leakRepo) }
        val monitorLeakUseCase by lazy { MonitorLeakUseCase(leakRepo) }
        val watchInstanceUseCase by lazy { WatchInstanceUseCase(leakRepo) }
        val monitorStatusUseCase by lazy { MonitorStatusUseCase(leakRepo) }
        val clearLeakedReferencesUseCase by lazy { ClearLeakedReferencesUseCase(leakRepo) }

        setContent {
            val viewModel: LeakViewModel = viewModel(
                factory = LeakViewModelFactory(
                    simulateLeakUseCase,
                    monitorLeakUseCase,
                    watchInstanceUseCase,
                    monitorStatusUseCase,
                    clearLeakedReferencesUseCase
                )
            )

            LaunchedEffect(key1 = Unit) {
                viewModel.navigateToMainEvent.collect {
                    finish()
                }
            }

            LeakScreen(
                viewModel = viewModel,
                onBack = { finish() }
            )
        }
    }
}

class LeakViewModelFactory(
    private val simulateMemoryLeakUseCase: SimulateMemoryLeakUseCase,
    private val monitorLeakUseCase: MonitorLeakUseCase,
    private val watchInstanceUseCase: WatchInstanceUseCase,
    private val monitorStatusUseCase: MonitorStatusUseCase,
    private val clearLeakedReferencesUseCase: ClearLeakedReferencesUseCase
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return LeakViewModel(
            simulateMemoryLeakUseCase,
            monitorLeakUseCase,
            watchInstanceUseCase,
            monitorStatusUseCase,
            clearLeakedReferencesUseCase
        ) as T
    }
}

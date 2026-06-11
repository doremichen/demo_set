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

package com.adam.app.demoset.performance.framework.benchmark

import com.adam.app.demoset.performance.domain.model.BenchmarkReport
import com.adam.app.demoset.performance.domain.model.BenchmarkType
import com.adam.app.demoset.performance.domain.repository.BenchmarkRepository

class BenchmarkRepositoryImpl : BenchmarkRepository {
    
    // In a real scenario, this would interact with AndroidFrameMetricsCollector
    // For this demo, we'll simulate the behavior if Activity is not readily available
    
    override fun startUiTracking() {
        // Start tracking logic
    }

    override fun stopUiTracking(scenarioName: String): BenchmarkReport {
        // Return a simulated UI report
        return BenchmarkReport(
            type = BenchmarkType.MACRO_UI,
            testScenarioName = scenarioName,
            medianFrameTimeMs = 8.3,
            executionTimeNs = 0L,
            jankCount = 2,
            totalFramesTracked = 120
        )
    }

    override fun runAlgorithmBenchmark(scenarioName: String, block: () -> Unit): BenchmarkReport {
        val startTime = System.nanoTime()
        block()
        val endTime = System.nanoTime()
        
        return BenchmarkReport(
            type = BenchmarkType.MICRO_ALGORITHM,
            testScenarioName = scenarioName,
            medianFrameTimeMs = 0.0,
            executionTimeNs = endTime - startTime,
            jankCount = 0,
            totalFramesTracked = 0
        )
    }
}

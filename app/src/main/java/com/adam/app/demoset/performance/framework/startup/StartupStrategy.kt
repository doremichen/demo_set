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

package com.adam.app.demoset.performance.framework.startup

import android.os.SystemClock
import com.adam.app.demoset.performance.domain.model.ExecutionResult
import com.adam.app.demoset.performance.domain.model.InitializationTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

interface StartupStrategy {
    suspend fun initialize(tasks: List<InitializationTask>): ExecutionResult
}

/**
 * sync blocking strategy
 */
class SyncBlockingStrategy : StartupStrategy {
    override suspend fun initialize(tasks: List<InitializationTask>): ExecutionResult {
        val startTime = SystemClock.elapsedRealtime();

        // monitor the current task duration
        for (task in tasks) {
            Thread.sleep(task.durationMs)
        }

        val endTime = SystemClock.elapsedRealtime();

        return ExecutionResult(
            timeElapsedMs = endTime - startTime,
            isJankOccurred = true,
            initializedTaskCount = tasks.size
        )

    }
}

/**
 * sync non-blocking strategy
 */
class AsyncStartupStrategy : StartupStrategy {
    override suspend fun initialize(tasks: List<InitializationTask>): ExecutionResult {
        return withContext(Dispatchers.IO) {
            // start time
            val startTime = SystemClock.elapsedRealtime();
            // monitor the current task duration
            val deferredTasks = tasks.map { task ->
                async {
                    Thread.sleep(task.durationMs)
                }
            }
            // wait all tasks to finish
            deferredTasks.awaitAll();

            // end time
            val endTime = SystemClock.elapsedRealtime();

            ExecutionResult(
                timeElapsedMs = endTime - startTime,
                isJankOccurred = false,
                initializedTaskCount = tasks.size
            )
        }
    }
}

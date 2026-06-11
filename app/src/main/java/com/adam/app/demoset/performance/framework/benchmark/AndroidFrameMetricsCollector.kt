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

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import android.view.FrameMetrics
import android.view.Window
import com.adam.app.demoset.performance.domain.model.BenchmarkReport
import com.adam.app.demoset.performance.domain.model.BenchmarkType

class AndroidFrameMetricsCollector {
    private var handlerThread: HandlerThread? = null
    private var frameMetricsListener: Window.OnFrameMetricsAvailableListener? = null
    private val frameDurations = mutableListOf<Long>()

    fun startCollection(activity: Activity) {
        // clear frame duration
        frameDurations.clear()
        // create handler thread
        handlerThread = HandlerThread("FrameMetricsWatcher").apply {
            start()
        }
        val handler = Handler(handlerThread!!.looper)

        frameMetricsListener = Window.OnFrameMetricsAvailableListener { _, frameMetrics, _ ->
            val durationNs = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
            val durationMs = durationNs / 1_000_000
            synchronized(frameDurations) {
                frameDurations.add(durationMs)
            }
        }

        activity.window.addOnFrameMetricsAvailableListener(frameMetricsListener!!, handler)
    }

    fun stopCollection(activity: Activity, scenarioName: String): BenchmarkReport {
        frameMetricsListener?.let {
            activity.window.removeOnFrameMetricsAvailableListener(it)
        }
        handlerThread?.quitSafely()

        val snapshot = synchronized(frameDurations) { frameDurations.toList() }
        val totalFrames = snapshot.size // 💡 取得總追蹤幀數

        if (snapshot.isEmpty()) {
            return BenchmarkReport(
                type = BenchmarkType.MACRO_UI,
                testScenarioName = scenarioName,
                medianFrameTimeMs = 0.0,
                executionTimeNs = 0L,
                jankCount = 0,
                totalFramesTracked = 0
            )
        }

        // 計算中位數
        val sorted = snapshot.sorted()
        val median = sorted[sorted.size / 2].toDouble()

        // 超過 16.6ms (在 60Hz 螢幕下) 即判定為掉幀 (Jank)
        val jankCount = snapshot.count { it > 16 }

        return BenchmarkReport(
            type = BenchmarkType.MACRO_UI,
            testScenarioName = scenarioName,
            medianFrameTimeMs = median,
            executionTimeNs = 0L,
            jankCount = jankCount,
            totalFramesTracked = totalFrames
        )
    }

}

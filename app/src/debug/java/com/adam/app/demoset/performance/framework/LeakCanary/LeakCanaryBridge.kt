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

package com.adam.app.demoset.performance.framework.LeakCanary

import com.adam.app.demoset.performance.domain.model.LeakConfig
import com.adam.app.demoset.performance.domain.model.LeakReport
import com.adam.app.demoset.performance.domain.model.LeakStatus
import com.adam.app.demoset.performance.domain.repository.LeakRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import leakcanary.AppWatcher
import java.lang.ref.WeakReference

class LeakCanaryBridge : LeakRepository {

    private val _leakReports = MutableSharedFlow<LeakReport>(replay = 1)
    private val _statusUpdates = MutableSharedFlow<LeakStatus>(replay = 1)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // used to make memory leak
    companion object {
        private val leakedLeash = mutableListOf<Any>()

        fun forceLeak(obj: Any) {
            leakedLeash.add(obj)
        }

        fun clearLeakedReferences() {
            leakedLeash.clear()
        }
    }

    override fun watchInstance(instance: Any, description: String) {
        // 1. Real call to LeakCanary framework
        AppWatcher.objectWatcher.expectWeaklyReachable(instance, description)

        // 2. Simulate internal analysis engine logic
        val weakRef = WeakReference(instance)
        // Important: Extract information before entering the coroutine to avoid the coroutine closure
        // holding a strong reference to the instance, which would cause analysis to fail.
        val className = instance.javaClass.simpleName 
        val estimatedSize = estimateObjectSize(instance)
        
        scope.launch {
            _statusUpdates.emit(LeakStatus.ALIVE)

            // Simulate LeakCanary wait period
            delay(LeakConfig.WATCH_WAIT_DELAY_MS)
            
            // Try to trigger GC so we can verify if the object is reclaimed
            System.gc()
            delay(LeakConfig.GC_WAIT_DELAY_MS)

            if (weakRef.get() == null) {
                // Report result: Object reclaimed normally
                _statusUpdates.emit(LeakStatus.DESTROYED)
            } else {
                // Report result: Leak detected!
                _statusUpdates.emit(LeakStatus.LEAKED)
                _leakReports.emit(
                    LeakReport(
                        targetClassName = className,
                        leakTraceShort = "Object retained: $description",
                        retainedHeapByteSize = estimatedSize,
                        status = LeakStatus.LEAKED
                    )
                )
            }
        }
    }

    /**
     * Simple reflection mechanism to estimate object size (for Demo)
     */
    private fun estimateObjectSize(obj: Any): Int {
        var size = LeakConfig.OBJECT_HEADER_SIZE // Base overhead for object header
        try {
            val fields = obj.javaClass.declaredFields
            for (field in fields) {
                field.isAccessible = true
                val value = field.get(obj)
                size += when (field.type) {
                    Int::class.java, Float::class.java -> LeakConfig.SIZE_INT_FLOAT
                    Long::class.java, Double::class.java -> LeakConfig.SIZE_LONG_DOUBLE
                    Boolean::class.java, Byte::class.java -> LeakConfig.SIZE_BOOLEAN_BYTE
                    Char::class.java, Short::class.java -> LeakConfig.SIZE_CHAR_SHORT
                    ByteArray::class.java -> (value as? ByteArray)?.size ?: 0
                    else -> LeakConfig.SIZE_REFERENCE // Object reference
                }
            }
        } catch (e: Exception) {
            size += LeakConfig.FALLBACK_OBJECT_SIZE // Fallback value on error
        }
        return size
    }

    override fun simulateLeak(instance: Any) {
        // Step 1: Manually create a strong reference (add to static list)
        forceLeak(instance)
        
        // Step 2: Hand over to watchInstance for monitoring, letting the engine "discover" the leak
        watchInstance(instance, "Simulated Leak via forceLeak")
    }

    override fun clearLeakedReferences() {
        Companion.clearLeakedReferences()
    }

    override fun getLeakReports(): Flow<LeakReport> = _leakReports

    override fun getStatusUpdates(): Flow<LeakStatus> = _statusUpdates
}

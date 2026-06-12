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

package com.adam.app.demoset.performance.domain.model

/**
 * Shared configuration for the LeakCanary Demo.
 */
object LeakConfig {
    // Analysis Timing
    const val WATCH_WAIT_DELAY_MS = 5000L
    const val GC_WAIT_DELAY_MS = 1000L

    // Memory Size Estimation (Bytes)
    const val OBJECT_HEADER_SIZE = 16
    const val SIZE_INT_FLOAT = 4
    const val SIZE_LONG_DOUBLE = 8
    const val SIZE_BOOLEAN_BYTE = 1
    const val SIZE_CHAR_SHORT = 2
    const val SIZE_REFERENCE = 4
    const val FALLBACK_OBJECT_SIZE = 1024

    // Dummy Target Simulation (KB)
    const val PAYLOAD_MIN_KB = 50
    const val PAYLOAD_MAX_KB = 500
    const val BYTES_PER_KB = 1024
}

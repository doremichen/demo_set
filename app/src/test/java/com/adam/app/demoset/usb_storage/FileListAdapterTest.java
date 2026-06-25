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
package com.adam.app.demoset.usb_storage;

import static org.junit.Assert.assertEquals;

import com.adam.app.demoset.usb_storage.adapter.FileListAdapter;

import org.junit.Test;

public class FileListAdapterTest {

    @Test
    public void testToFileSizeInfo() {
        assertEquals("0 B", FileListAdapter.toFileSizeInfo(0));
        assertEquals("0 B", FileListAdapter.toFileSizeInfo(-1));
        assertEquals("512.00 B", FileListAdapter.toFileSizeInfo(512));
        assertEquals("1.00 kB", FileListAdapter.toFileSizeInfo(1024));
        assertEquals("1.00 MB", FileListAdapter.toFileSizeInfo(1024 * 1024));
        assertEquals("1.00 GB", FileListAdapter.toFileSizeInfo(1024L * 1024 * 1024));
        assertEquals("1.50 MB", FileListAdapter.toFileSizeInfo((long) (1.5 * 1024 * 1024)));
    }
}

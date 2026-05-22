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

package com.adam.app.demoset.usb_storage.model;

import androidx.annotation.DrawableRes;

public class FileItem {
    private final String mName;
    private final String mSize;
    private final @DrawableRes int mIconResId;
    private final Object mOriginalFile; // Can be java.io.File or UsbFile
    private final boolean mIsDirectory;

    public FileItem(String name, String size, int iconResId, Object originalFile, boolean isDirectory) {
        this.mName = name;
        this.mSize = size;
        this.mIconResId = iconResId;
        this.mOriginalFile = originalFile;
        this.mIsDirectory = isDirectory;
    }

    public String getName() {
        return mName;
    }

    public String getSize() {
        return mSize;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public Object getOriginalFile() {
        return mOriginalFile;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }
}

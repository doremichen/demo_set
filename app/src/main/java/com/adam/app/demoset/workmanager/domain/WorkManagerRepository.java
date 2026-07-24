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

package com.adam.app.demoset.workmanager.domain;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.work.WorkInfo;

import java.util.List;

/**
 * Repository interface for WorkManager operations.
 */
public interface WorkManagerRepository {
    /**
     * Apply blur to the image at the given Uri.
     *
     * @param imageUri  The Uri of the image to blur.
     * @param blurLevel The level of blur to apply.
     */
    void applyBlur(Uri imageUri, int blurLevel);

    /**
     * Cancel the blur work.
     */
    void cancelWork();

    /**
     * Get LiveData for WorkInfo by tag.
     *
     * @param tag The tag to filter by.
     * @return LiveData containing the list of WorkInfo.
     */
    LiveData<List<WorkInfo>> getWorkInfosByTag(String tag);

    /**
     * Prune finished work.
     */
    void pruneWork();
}

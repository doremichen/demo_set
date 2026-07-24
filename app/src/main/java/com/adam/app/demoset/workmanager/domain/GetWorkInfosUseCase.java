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

import androidx.lifecycle.LiveData;
import androidx.work.WorkInfo;

import java.util.List;

import javax.inject.Inject;

/**
 * Use case to get work infos by tag.
 */
public class GetWorkInfosUseCase {
    private final WorkManagerRepository mRepository;

    @Inject
    public GetWorkInfosUseCase(WorkManagerRepository repository) {
        this.mRepository = repository;
    }

    /**
     * Executes the get work infos logic.
     *
     * @param tag The tag.
     * @return LiveData of work infos.
     */
    public LiveData<List<WorkInfo>> execute(String tag) {
        return mRepository.getWorkInfosByTag(tag);
    }
}

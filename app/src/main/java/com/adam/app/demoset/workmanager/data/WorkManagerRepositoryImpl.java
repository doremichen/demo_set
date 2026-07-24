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

package com.adam.app.demoset.workmanager.data;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.workmanager.data.workers.BlurWorker;
import com.adam.app.demoset.workmanager.data.workers.CleanupWorker;
import com.adam.app.demoset.workmanager.data.workers.SaveToFileWorker;
import com.adam.app.demoset.workmanager.domain.WorkManagerRepository;

import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Implementation of WorkManagerRepository.
 */
@Singleton
public class WorkManagerRepositoryImpl implements WorkManagerRepository {
    private final WorkManager mWorkManager;

    @Inject
    public WorkManagerRepositoryImpl(@ApplicationContext Context context) {
        this.mWorkManager = WorkManager.getInstance(context);
    }

    @Override
    public void applyBlur(Uri imageUri, int blurLevel) {
        // Start with cleanup work request
        WorkContinuation continuation = mWorkManager.beginUniqueWork(
                DemoAppConstants.IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker.class)
        );

        // Chain blur work requests based on the level
        continuation = IntStream.range(0, blurLevel)
                .mapToObj(i -> new OneTimeWorkRequest.Builder(BlurWorker.class)
                        .setInputData(i == 0 ? createInputDataForUri(imageUri) : Data.EMPTY)
                        .build())
                .reduce(continuation, WorkContinuation::then, (a, b) -> b);

        // Constraints and save request
        OneTimeWorkRequest saveReq = new OneTimeWorkRequest.Builder(SaveToFileWorker.class)
                .setConstraints(new Constraints.Builder().setRequiresCharging(false).build())
                .addTag(DemoAppConstants.TAG_IMG_OUTPUT)
                .build();

        // Chain save request and start work process
        continuation.then(saveReq).enqueue();
    }

    @Override
    public void cancelWork() {
        mWorkManager.cancelUniqueWork(DemoAppConstants.IMAGE_MANIPULATION_WORK_NAME);
    }

    @Override
    public LiveData<List<WorkInfo>> getWorkInfosByTag(String tag) {
        return mWorkManager.getWorkInfosByTagLiveData(tag);
    }

    @Override
    public void pruneWork() {
        mWorkManager.pruneWork();
    }

    private Data createInputDataForUri(Uri imageUri) {
        Data.Builder data = new Data.Builder();
        if (imageUri != null) {
            data.putString(DemoAppConstants.THE_SELECTED_IMAGE, imageUri.toString());
        }
        return data.build();
    }
}

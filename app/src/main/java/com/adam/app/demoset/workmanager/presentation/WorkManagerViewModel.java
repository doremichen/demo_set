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

package com.adam.app.demoset.workmanager.presentation;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkInfo;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.workmanager.domain.ApplyBlurUseCase;
import com.adam.app.demoset.workmanager.domain.CancelWorkUseCase;
import com.adam.app.demoset.workmanager.domain.GetWorkInfosUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for WorkManager demo.
 */
@HiltViewModel
public class WorkManagerViewModel extends AndroidViewModel {

    public static final int BLUR_LEVEL_1 = 1;
    public static final int BLUR_LEVEL_2 = 2;
    public static final int BLUR_LEVEL_3 = 3;

    private final ApplyBlurUseCase mApplyBlurUseCase;
    private final CancelWorkUseCase mCancelWorkUseCase;
    private final GetWorkInfosUseCase mGetWorkInfosUseCase;
    private final LiveData<List<WorkInfo>> mSaveWorkInfo;
    private final MutableLiveData<Uri> mOutputUri = new MutableLiveData<>();
    private Uri mImageUri;

    @Inject
    public WorkManagerViewModel(@NonNull Application app,
                                ApplyBlurUseCase applyBlurUseCase,
                                CancelWorkUseCase cancelWorkUseCase,
                                GetWorkInfosUseCase getWorkInfosUseCase) {
        super(app);
        this.mApplyBlurUseCase = applyBlurUseCase;
        this.mCancelWorkUseCase = cancelWorkUseCase;
        this.mGetWorkInfosUseCase = getWorkInfosUseCase;

        // Build default image URI
        this.mImageUri = buildImageUri(app.getApplicationContext());

        // Observe work changes
        this.mSaveWorkInfo = mGetWorkInfosUseCase.execute(DemoAppConstants.TAG_IMG_OUTPUT);
    }

    public void updateImgUri(@NonNull Uri imgUri) {
        this.mImageUri = imgUri;
    }

    public LiveData<List<WorkInfo>> getWorkInfo() {
        return mSaveWorkInfo;
    }

    public LiveData<Uri> getOutputUri() {
        return mOutputUri;
    }

    public void setOutputUri(String outputUri) {
        if (!TextUtils.isEmpty(outputUri)) {
            mOutputUri.setValue(Uri.parse(outputUri));
        }
    }

    public void applyBlur(int level) {
        mApplyBlurUseCase.execute(mImageUri, level);
    }

    public void cancelWork() {
        mCancelWorkUseCase.execute();
    }

    private Uri buildImageUri(Context ctx) {
        Resources resources = ctx.getResources();
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.test))
                .appendPath(resources.getResourceTypeName(R.drawable.test))
                .appendPath(resources.getResourceEntryName(R.drawable.test))
                .build();
    }
}

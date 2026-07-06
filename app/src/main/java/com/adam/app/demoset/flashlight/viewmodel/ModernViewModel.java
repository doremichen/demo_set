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

package com.adam.app.demoset.flashlight.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import com.adam.app.demoset.flashlight.data.FlashLightMetadata;
import com.adam.app.demoset.flashlight.domain.usecase.FlashLightUseCase;
import com.adam.app.demoset.flashlight.domain.usecase.ModernToggleUseCase;
import java.util.List;

/**
 * ViewModel for Modern Flashlight implementation
 */
public class ModernViewModel extends AndroidViewModel {
    private final FlashLightUseCase mUseCase;
    private final LiveData<List<WorkInfo>> mWorkInfos;

    public ModernViewModel(@NonNull Application application) {
        super(application);
        this.mUseCase = new ModernToggleUseCase();
        this.mWorkInfos = WorkManager.getInstance(application)
                .getWorkInfosByTagLiveData(FlashLightMetadata.TAG_FLASH_LIGHT_WORK);
    }

    public void toggleFlashlight(boolean enabled) {
        mUseCase.execute(getApplication(), enabled);
    }

    public LiveData<List<WorkInfo>> getWorkInfos() {
        return mWorkInfos;
    }
}

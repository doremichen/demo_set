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

package com.adam.app.demoset.flashlight.domain.usecase;

import android.content.Context;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.adam.app.demoset.flashlight.data.FlashLightMetadata;
import com.adam.app.demoset.flashlight.domain.FlashLightWork;

/**
 * Modern Use Case using WorkManager
 */
public class ModernToggleUseCase implements FlashLightUseCase {
    @Override
    public void execute(Context context, boolean enabled) {
        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());
        Data inputData = new Data.Builder()
                .putBoolean(FlashLightMetadata.KEY_ON, enabled)
                .build();
        
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(FlashLightWork.class)
                .setInputData(inputData)
                .addTag(FlashLightMetadata.TAG_FLASH_LIGHT_WORK)
                .build();
        
        workManager.enqueue(workRequest);
    }
}

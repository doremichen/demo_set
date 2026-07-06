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

package com.adam.app.demoset.flashlight.domain;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.flashlight.data.FlashLightMetadata;
import com.adam.app.demoset.utils.Utils;

/**
 * Flashlight Worker
 * Located in domain/controller as it executes the flashlight logic.
 */
public class FlashLightWork extends Worker {

    private final CameraManager mCameraManger;

    public FlashLightWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mCameraManger = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.info(this, "doWork");
        boolean state = this.getInputData().getBoolean(FlashLightMetadata.KEY_ON, false);
        Utils.info(this, "flash light state: " + state);
        
        try {
            String cameraId = this.mCameraManger.getCameraIdList()[0];
            this.mCameraManger.setTorchMode(cameraId, state);
            return Result.success();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}

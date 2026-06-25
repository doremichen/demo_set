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
package com.adam.app.demoset.camera2.controller;

import android.hardware.camera2.CaptureResult;

/**
 * Interface for Camera States (State Pattern).
 */
public interface CameraState {
    void process(CaptureResult result, MyCameraController controller);
}

class PreviewState implements CameraState {
    @Override
    public void process(CaptureResult result, MyCameraController controller) {
        // Do nothing in preview
    }
}

class WaitingLockState implements CameraState {
    @Override
    public void process(CaptureResult result, MyCameraController controller) {
        Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
        if (afState == null) {
            controller.captureStillPicture();
        } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
            if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                controller.setCameraState(new PictureTakenState());
                controller.captureStillPicture();
            } else {
                controller.runPrecaptureSequence();
            }
        }
    }
}

class WaitingPrecaptureState implements CameraState {
    @Override
    public void process(CaptureResult result, MyCameraController controller) {
        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
        if (aeState == null ||
                aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                aeState == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED) {
            controller.setCameraState(new WaitingNonPrecaptureState());
        }
    }
}

class WaitingNonPrecaptureState implements CameraState {
    @Override
    public void process(CaptureResult result, MyCameraController controller) {
        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
        if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
            controller.setCameraState(new PictureTakenState());
            controller.captureStillPicture();
        }
    }
}

class PictureTakenState implements CameraState {
    @Override
    public void process(CaptureResult result, MyCameraController controller) {
        // Picture already taken
    }
}

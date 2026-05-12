/*
 * Copyright (c) 2026 Adam Chen
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

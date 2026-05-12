/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.camera2.controller;

/**
 * Interface to manage camera state transitions (Context part of State Pattern).
 */
public interface CameraStateContext {
    void setState(CameraState state);
    void captureStillPicture();
    void runPrecaptureSequence();
    void unlockFocus();
}

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

package com.adam.app.demoset.video.domain.usecase;

import android.content.Context;
import android.util.Size;
import android.view.TextureView;

import com.adam.app.demoset.video.domain.repository.VideoRecordListener;
import com.adam.app.demoset.video.domain.repository.VideoRepository;

import javax.inject.Inject;

/**
 * Use case to manage camera hardware operations.
 */
public class CameraUseCase {
    private final VideoRepository mRepository;

    @Inject
    public CameraUseCase(VideoRepository repository) {
        this.mRepository = repository;
    }

    public void startThread() {
        mRepository.startCameraThread();
    }

    public void stopThread() {
        mRepository.stopCameraThread();
    }

    public void openCamera(Context context, TextureView textureView) {
        mRepository.openCamera(context, textureView);
    }

    public void closeCamera() {
        mRepository.closeCamera();
    }

    public void registerListener(VideoRecordListener listener) {
        mRepository.registerListener(listener);
    }

    public void unregisterListener(VideoRecordListener listener) {
        mRepository.unregisterListener(listener);
    }

    public Size getPreviewSize() {
        return mRepository.getPreviewSize();
    }

    public void configureTransform(int width, int height, int rotation) {
        mRepository.configureTransform(width, height, rotation);
    }
}

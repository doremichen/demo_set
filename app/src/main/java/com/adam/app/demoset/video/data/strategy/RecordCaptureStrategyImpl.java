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

package com.adam.app.demoset.video.data.strategy;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.video.domain.strategy.CaptureStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Concrete implementation of Recording strategy.
 * Part of Data layer.
 */
public class RecordCaptureStrategyImpl implements CaptureStrategy {
    private final TextureView mTextureView;
    private final Size mPreviewSize;
    private final MediaRecorder mRecorder;

    public RecordCaptureStrategyImpl(TextureView textureView, Size previewSize, MediaRecorder recorder) {
        this.mTextureView = textureView;
        this.mPreviewSize = previewSize;
        this.mRecorder = recorder;
    }

    @Override
    public int getTemplateType() {
        return CameraDevice.TEMPLATE_RECORD;
    }

    @Override
    public List<Surface> getSurfaces() {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if (texture == null) return new ArrayList<>();
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(texture);
        Surface recordSurface = mRecorder.getSurface();
        return Arrays.asList(previewSurface, recordSurface);
    }

    @Override
    public void onConfigured(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    @Override
    public void onSessionStarted(Context context) {
        new Handler(context.getMainLooper()).post(() -> {
            try {
                mRecorder.start();
                Utils.showToast(context, context.getString(R.string.demo_video_record_recording));
            } catch (IllegalStateException e) {
                Utils.error(this, "Failed to start recorder: " + e.getMessage());
            }
        });
    }
}

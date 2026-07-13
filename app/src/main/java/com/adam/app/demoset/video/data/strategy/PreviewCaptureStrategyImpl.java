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

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.adam.app.demoset.video.domain.strategy.CaptureStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete implementation of Preview strategy.
 * Part of Data layer.
 */
public class PreviewCaptureStrategyImpl implements CaptureStrategy {
    private final TextureView mTextureView;
    private final Size mPreviewSize;

    public PreviewCaptureStrategyImpl(TextureView textureView, Size previewSize) {
        this.mTextureView = textureView;
        this.mPreviewSize = previewSize;
    }

    @Override
    public int getTemplateType() {
        return CameraDevice.TEMPLATE_PREVIEW;
    }

    @Override
    public List<Surface> getSurfaces() {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if (texture == null) return new ArrayList<>();
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        return Collections.singletonList(new Surface(texture));
    }

    @Override
    public void onConfigured(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }
}

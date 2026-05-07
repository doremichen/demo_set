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

package com.adam.app.demoset.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.adam.app.demoset.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for video recording operations using Camera2 API.
 * Implemented as a Singleton.
 */
public class MyRecordVideoController {

    private HandlerThread mBgThread;
    private Handler mHandler;
    private Size mPreviewSize;
    private MediaRecorder mRecorder;
    private CameraDevice mDevice;
    private ControllerListener mListener;
    private TextureView mTextureView;
    private CaptureRequest.Builder mRequestBuilder;
    private CameraCaptureSession mPreviewSession;
    private Size mVideoSize;

    private enum RecordState {
        START,
        STOP
    }

    private RecordState mRecordState = RecordState.STOP;

    private final CameraDevice.StateCallback mOpenCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Utils.info(this, "Camera onOpened");
            mDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Utils.info(this, "Camera onDisconnected");
            camera.close();
            mDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Utils.error(this, "Camera onError: " + error);
            camera.close();
            mDevice = null;
            if (mListener != null) {
                mListener.onError(error);
            }
        }
    };

    private MyRecordVideoController() {
    }

    private static class Helper {
        private static final MyRecordVideoController INSTANCE = new MyRecordVideoController();
    }

    public static MyRecordVideoController newInstance() {
        return Helper.INSTANCE;
    }

    public void startCameraThread() {
        Utils.info(this, "startCameraThread");
        if (mBgThread == null) {
            mBgThread = new HandlerThread("camera work thread");
            mBgThread.start();
            mHandler = new Handler(mBgThread.getLooper());
        }
    }

    public void stopCameraThread() {
        Utils.info(this, "stopCameraThread");
        if (mBgThread != null) {
            mBgThread.quitSafely();
            try {
                mBgThread.join();
                mHandler = null;
                mBgThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void openCamera(Context context, TextureView textureView) {
        Utils.info(this, "openCamera");
        mTextureView = textureView;
        CameraManager cameraService = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraService.getCameraIdList()[0]; // Default to first camera (usually back)
            CameraCharacteristics cameraChar = cameraService.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            
            if (map != null) {
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            }

            mRecorder = new MediaRecorder();
            cameraService.openCamera(cameraId, mOpenCameraCallback, mHandler);
        } catch (CameraAccessException e) {
            Utils.error(this, "CameraAccessException: " + e.getMessage());
        }
    }

    public void closeCamera() {
        Utils.info(this, "closeCamera");
        closePreviewSession();
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        mRecordState = RecordState.STOP;
    }

    public boolean isRecording() {
        return this.mRecordState == RecordState.START;
    }

    public void startRecord(final Activity activity) {
        Utils.info(this, "startRecord");
        if (!Utils.areAllNotNull(mDevice, mPreviewSize, mListener) || !mTextureView.isAvailable()) {
            Utils.error(this, "Validation failed for startRecord");
            return;
        }

        if (isRecording()) {
            mListener.onInfo("Already recording");
            return;
        }

        try {
            closePreviewSession();
            setUpVideoConfig();

            CaptureRequestStrategy strategy = new RecordCaptureRequestStrategy();
            strategy.createCaptureRequest();

            startRecordSession(activity, strategy);
            this.mRecordState = RecordState.START;
        } catch (CameraAccessException | IOException e) {
            Utils.error(this, "Exception during startRecord: " + e.getMessage());
        }
    }

    public void stopRecord() {
        Utils.info(this, "stopRecord");
        if (mRecorder == null || !isRecording()) {
            Utils.info(this, "Not recording or recorder null");
            return;
        }

        try {
            mRecorder.setOnErrorListener(null);
            mRecorder.setOnInfoListener(null);
            mRecorder.stop();
            mRecorder.reset();
            mListener.onInfo("Recording stopped");
        } catch (RuntimeException e) {
            Utils.error(this, "RuntimeException during recorder stop: " + e.getMessage());
        }

        this.mRecordState = RecordState.STOP;
        startPreview();
    }

    private void startPreview() {
        Utils.info(this, "startPreview");
        if (mDevice == null || mTextureView == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }

        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            mRequestBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface previewSurface = new Surface(texture);
            mRequestBuilder.addTarget(previewSurface);

            mDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    if (mListener != null) mListener.onFail("Preview config fail");
                }
            }, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        // Fix: Added null checks for mDevice and mPreviewSession to prevent IllegalStateException
        if (mDevice == null || mPreviewSession == null || mRequestBuilder == null) {
            return;
        }

        try {
            mRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mPreviewSession.setRepeatingRequest(mRequestBuilder.build(), null, mHandler);
        } catch (CameraAccessException | IllegalStateException e) {
            Utils.error(this, "Error updating preview: " + e.getMessage());
        }
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private void setUpVideoConfig() throws IOException {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mListener.getPath());
        mRecorder.setVideoEncodingBitRate(10000000);
        mRecorder.setVideoFrameRate(30);
        mRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOrientationHint(90);
        mRecorder.prepare();
    }

    private void startRecordSession(final Activity activity, CaptureRequestStrategy strategy) throws CameraAccessException {
        mDevice.createCaptureSession(strategy.getSurfaces(), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mPreviewSession = session;
                updatePreview();
                activity.runOnUiThread(() -> {
                    try {
                        mRecorder.start();
                        Utils.showToast(activity, "Recording...");
                    } catch (IllegalStateException e) {
                        Utils.error(this, "Failed to start recorder: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                if (mListener != null) mListener.onFail("Record session failed");
            }
        }, mHandler);
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    public void registerListener(ControllerListener listener) {
        mListener = listener;
    }

    interface CaptureRequestStrategy {
        void createCaptureRequest() throws CameraAccessException;
        List<Surface> getSurfaces();
    }

    private class RecordCaptureRequestStrategy implements CaptureRequestStrategy {
        @Override
        public void createCaptureRequest() throws CameraAccessException {
            mRequestBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(texture);
            Surface recordSurface = mRecorder.getSurface();
            mRequestBuilder.addTarget(previewSurface);
            mRequestBuilder.addTarget(recordSurface);
        }

        @Override
        public List<Surface> getSurfaces() {
            Surface previewSurface = new Surface(mTextureView.getSurfaceTexture());
            Surface recordSurface = mRecorder.getSurface();
            return new ArrayList<>(Arrays.asList(previewSurface, recordSurface));
        }
    }

    public interface ControllerListener {
        void onError(int result);
        void onFail(String msg);
        void onInfo(String msg);
        String getPath();
    }
}

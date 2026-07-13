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

package com.adam.app.demoset.video.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.video.data.strategy.PreviewCaptureStrategyImpl;
import com.adam.app.demoset.video.data.strategy.RecordCaptureStrategyImpl;
import com.adam.app.demoset.video.domain.repository.VideoRecordListener;
import com.adam.app.demoset.video.domain.repository.VideoRepository;
import com.adam.app.demoset.video.domain.strategy.CaptureStrategy;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of VideoRepository using Camera2 API.
 * High cohesion achieved by focusing on camera operations.
 */
@Singleton
public class VideoRepositoryImpl implements VideoRepository {

    private static final String CAMERA_WORK_THREAD_NAME = DemoAppConstants.THREAD_NAME_VIDEO;

    private static final int DEFAULT_SENSOR_ORIENTATION = 0;
    private static final int VIDEO_BIT_RATE = 10000000;
    private static final int VIDEO_FRAME_RATE = 30;
    private static final int ORIENTATION_90 = 90;
    private static final int ORIENTATION_180 = 180;
    private static final int ORIENTATION_270 = 270;
    private static final int ASPECT_RATIO_WIDTH = 4;
    private static final int ASPECT_RATIO_HEIGHT = 3;
    private static final int MAX_VIDEO_WIDTH = 1080;

    private HandlerThread mBgThread;
    private Handler mHandler;
    private Size mPreviewSize;
    private MediaRecorder mRecorder;
    private CameraDevice mDevice;
    private VideoRecordListener mListener;
    private TextureView mTextureView;
    private CaptureRequest.Builder mRequestBuilder;
    private CameraCaptureSession mPreviewSession;
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
    private Size mVideoSize;
    private int mSensorOrientation = DEFAULT_SENSOR_ORIENTATION;
    private RecordState mRecordState = RecordState.STOP;

    @Inject
    public VideoRepositoryImpl() {
        // Hilt injection constructor
    }

    @Override
    public void startCameraThread() {
        Utils.info(this, "startCameraThread");
        if (mBgThread != null) return;
        mBgThread = new HandlerThread(CAMERA_WORK_THREAD_NAME);
        mBgThread.start();
        mHandler = new Handler(mBgThread.getLooper());
    }

    @Override
    public void stopCameraThread() {
        Utils.info(this, "stopCameraThread");
        if (mBgThread == null) return;
        mBgThread.quitSafely();
        try {
            mBgThread.join();
            mHandler = null;
            mBgThread = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public void openCamera(Context context, TextureView textureView) {
        Utils.info(this, "openCamera");
        mTextureView = textureView;
        CameraManager cameraService = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraService.getCameraIdList()[0];
            CameraCharacteristics cameraChar = cameraService.getCameraCharacteristics(cameraId);
            Integer sensorOrientation = cameraChar.get(CameraCharacteristics.SENSOR_ORIENTATION);
            mSensorOrientation = sensorOrientation != null ? sensorOrientation : DEFAULT_SENSOR_ORIENTATION;

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

    @Override
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
        mTextureView = null;
    }

    @Override
    public boolean isRecording() {
        return this.mRecordState == RecordState.START;
    }

    @Override
    public void startPreview() {
        Utils.info(this, "startPreview");
        if (mDevice == null || mTextureView == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }
        executeStrategy(null, new PreviewCaptureStrategyImpl(mTextureView, mPreviewSize));
    }

    @Override
    public void startRecord(Context context) {
        Utils.info(this, "startRecord");
        if (!Utils.areAllNotNull(mDevice, mPreviewSize, mListener) || !mTextureView.isAvailable()) {
            Utils.error(this, "Validation failed for startRecord");
            return;
        }

        if (isRecording()) {
            mListener.onInfo(R.string.demo_video_record_info_already_recording);
            return;
        }

        try {
            setUpVideoConfig();
            executeStrategy(context, new RecordCaptureStrategyImpl(mTextureView, mPreviewSize, mRecorder));
            this.mRecordState = RecordState.START;
        } catch (IOException e) {
            Utils.error(this, "IOException during startRecord: " + e.getMessage());
        }
    }

    @Override
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
            mListener.onInfo(R.string.demo_video_record_info_stopped);
        } catch (RuntimeException e) {
            Utils.error(this, "RuntimeException during recorder stop: " + e.getMessage());
        }

        this.mRecordState = RecordState.STOP;
        startPreview();
    }

    @Override
    public void executeStrategy(final Context context, CaptureStrategy strategy) {
        try {
            closePreviewSession();
            mRequestBuilder = mDevice.createCaptureRequest(strategy.getTemplateType());
            List<Surface> surfaces = strategy.getSurfaces();
            for (Surface surface : surfaces) {
                mRequestBuilder.addTarget(surface);
            }

            mDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mPreviewSession = session;
                    strategy.onConfigured(mRequestBuilder);
                    updatePreview();
                    strategy.onSessionStarted(context);
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    if (mListener != null) {
                        int errorRes = (strategy instanceof RecordCaptureStrategyImpl) ?
                                R.string.demo_video_record_fail_session : R.string.demo_video_record_fail_preview;
                        mListener.onFail(errorRes);
                    }
                }
            }, mHandler);
        } catch (CameraAccessException e) {
            Utils.error(this, "CameraAccessException in executeStrategy: " + e.getMessage());
        }
    }

    private void updatePreview() {
        if (mDevice == null || mPreviewSession == null || mRequestBuilder == null) return;
        try {
            mPreviewSession.setRepeatingRequest(mRequestBuilder.build(), null, mHandler);
        } catch (CameraAccessException | IllegalStateException e) {
            Utils.error(this, "Error updating preview: " + e.getMessage());
        }
    }

    private void closePreviewSession() {
        if (mPreviewSession == null) return;
        mPreviewSession.close();
        mPreviewSession = null;
    }

    private void setUpVideoConfig() throws IOException {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mListener.getPath());
        mRecorder.setVideoEncodingBitRate(VIDEO_BIT_RATE);
        mRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
        mRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOrientationHint(ORIENTATION_90);
        mRecorder.prepare();
    }

    private Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * ASPECT_RATIO_WIDTH / ASPECT_RATIO_HEIGHT &&
                    size.getWidth() <= MAX_VIDEO_WIDTH) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    @Override
    public void registerListener(VideoRecordListener listener) {
        mListener = listener;
    }

    @Override
    public void unregisterListener(VideoRecordListener listener) {
        if (mListener == listener) mListener = null;
    }

    @Override
    public Size getPreviewSize() {
        return mPreviewSize;
    }

    @Override
    public void configureTransform(int viewWidth, int viewHeight, int rotation) {
        if (null == mTextureView || null == mPreviewSize) return;

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        float rotatedBufferWidth = isDimensionSwapped(rotation) ? mPreviewSize.getHeight() : mPreviewSize.getWidth();
        float rotatedBufferHeight = isDimensionSwapped(rotation) ? mPreviewSize.getWidth() : mPreviewSize.getHeight();

        float scaleX = 1.0f, scaleY = 1.0f;
        float viewRatio = (float) viewWidth / viewHeight;
        float bufferRatio = rotatedBufferWidth / rotatedBufferHeight;

        if (viewRatio > bufferRatio) {
            scaleY = (viewWidth / rotatedBufferWidth) / (viewHeight / rotatedBufferHeight);
        } else {
            scaleX = (viewHeight / rotatedBufferHeight) / (viewWidth / rotatedBufferWidth);
        }

        matrix.postScale(scaleX, scaleY, centerX, centerY);
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            matrix.postRotate(ORIENTATION_90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(ORIENTATION_180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private boolean isDimensionSwapped(int rotation) {
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            return mSensorOrientation == ORIENTATION_90 || mSensorOrientation == ORIENTATION_270;
        }
        return mSensorOrientation == 0 || mSensorOrientation == ORIENTATION_180;
    }

    private enum RecordState {
        START,
        STOP
    }
}

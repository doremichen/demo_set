/*
 * Copyright (c) 2026 Adam Chen
 */

package com.adam.app.demoset.camera2.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.adam.app.demoset.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for Camera2 operations.
 */
public final class MyCameraController {

    private static final String TAG = "MyCameraController";
    private static final String THREAD_NAME = "CameraBackground";
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private WeakReference<TextureView> mTextureViewRef;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private Size mPreviewSize;
    private ImageReader mImageReader;
    private int mSensorOrientation = 0;

    private CameraState mState = new PreviewState();
    private boolean mFlashSupported;
    private CameraCallback mCallback;

    private MyCameraController() {}

    private static class Holder {
        private static final MyCameraController INSTANCE = new MyCameraController();
    }

    public static MyCameraController getInstance() {
        return Holder.INSTANCE;
    }

    public void setCameraState(CameraState state) {
        this.mState = state;
    }

    public void startCameraThread() {
        mBackgroundThread = new HandlerThread(THREAD_NAME);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    public void stopCameraThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while stopping camera thread", e);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void openCamera(Context context, int lensFacing) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = findCameraId(manager, lensFacing);
            if (cameraId == null) return;

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            setupCameraOutputs(characteristics);
            
            configureTransformFromContext(context);

            manager.openCamera(cameraId, mDeviceStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Cannot access the camera", e);
        }
    }

    private String findCameraId(CameraManager manager, int lensFacing) throws CameraAccessException {
        for (String id : manager.getCameraIdList()) {
            CameraCharacteristics c = manager.getCameraCharacteristics(id);
            Integer facing = c.get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == lensFacing) return id;
        }
        return manager.getCameraIdList().length > 0 ? manager.getCameraIdList()[0] : null;
    }

    private void setupCameraOutputs(CameraCharacteristics characteristics) {
        mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) != null ?
                characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) : 0;

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map == null) return;

        Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
        mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 2);
        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

        TextureView textureView = getTextureView();
        if (textureView == null) return;

        mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                textureView.getWidth(), textureView.getHeight(), MAX_PREVIEW_WIDTH, MAX_PREVIEW_HEIGHT, largest);

        Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        mFlashSupported = available != null && available;
    }

    private void configureTransformFromContext(Context context) {
        TextureView textureView = getTextureView();
        if (textureView != null && context instanceof Activity) {
            configureTransform(textureView.getWidth(), textureView.getHeight(), (Activity) context);
        }
    }

    public void startPreview() {
        try {
            Surface surface = preparePreviewSurface();
            if (surface == null) return;

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    mSessionStateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error starting preview", e);
        }
    }

    private Surface preparePreviewSurface() {
        TextureView textureView = getTextureView();
        if (textureView == null) return null;
        SurfaceTexture texture = textureView.getSurfaceTexture();
        if (texture == null) return null;
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        return new Surface(texture);
    }

    private final CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            if (null == mCameraDevice) return;
            mCaptureSession = session;
            try {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                setAutoFlash(mPreviewRequestBuilder);
                mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                Log.e(TAG, "Error starting repeating request", e);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            if (mCallback != null) mCallback.info("Configuration failed");
        }
    };

    public void configureTransform(int viewWidth, int viewHeight, Activity activity) {
        TextureView textureView = getTextureView();
        if (null == textureView || null == mPreviewSize || null == activity) return;

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private boolean isDimensionSwapped(int rotation) {
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            return mSensorOrientation == 90 || mSensorOrientation == 270;
        } else {
            return mSensorOrientation == 0 || mSensorOrientation == 180;
        }
    }

    public void captureStillPicture() {
        try {
            if (null == mCameraDevice) return;
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    if (mCallback != null) mCallback.onCaptureDone();
                    unlockFocus();
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error capturing picture", e);
        }
    }

    private void unlockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            mState = new PreviewState();
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error unlocking focus", e);
        }
    }

    public void runPrecaptureSequence() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = new WaitingPrecaptureState();
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error running precapture sequence", e);
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    public void closeCamera() {
        if (null != mCaptureSession) { mCaptureSession.close(); mCaptureSession = null; }
        if (null != mCameraDevice) { mCameraDevice.close(); mCameraDevice = null; }
        if (null != mImageReader) { mImageReader.close(); mImageReader = null; }
    }

    private final CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
            if (mCallback != null) mCallback.onDeviceStateError(error);
        }
    };

    private final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            mState.process(partialResult, MyCameraController.this);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            mState.process(result, MyCameraController.this);
        }
    };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            String path = mCallback != null ? mCallback.getPath() : null;
            if (path == null) return;
            try (Image image = reader.acquireLatestImage(); OutputStream output = new FileOutputStream(new File(path))) {
                if (image == null) return;
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                output.write(bytes);
                if (mCallback != null) mCallback.onSaveImageComplete();
            } catch (IOException e) {
                Log.e(TAG, "Error saving image", e);
            }
        }
    };

    public void setPreviewContent(TextureView view) {
        mTextureViewRef = new WeakReference<>(view);
    }

    private TextureView getTextureView() {
        return mTextureViewRef != null ? mTextureViewRef.get() : null;
    }

    public void registerCallback(CameraCallback callback) {
        mCallback = callback;
    }

    public void capturePicture() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = new WaitingLockState();
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error locking focus", e);
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth(), h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight && option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) bigEnough.add(option);
                else notBigEnough.add(option);
            }
        }
        if (!bigEnough.isEmpty()) return Collections.min(bigEnough, new CompareSizesByArea());
        if (!notBigEnough.isEmpty()) return Collections.max(notBigEnough, new CompareSizesByArea());
        return choices[0];
    }

    public interface CameraCallback {
        void onCaptureDone();
        void info(String str);
        void onDeviceStateError(int code);
        String getPath();
        void onSaveImageComplete();
    }

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}

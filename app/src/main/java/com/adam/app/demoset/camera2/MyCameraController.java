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

package com.adam.app.demoset.camera2;

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
 * Fixed preview stretching using Center-Crop transformation.
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

    private State mState = State.PREVIEW;
    private boolean mFlashSupported;
    private CameraCallback mCallback;

    private enum State {
        PREVIEW,
        WAITING_LOCK,
        WAITING_PRECAPTURE,
        WAITING_NON_PRECAPTURE,
        PICTURE_TAKEN
    }

    private final CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            Utils.info(TAG, "onOpened");
            mCameraDevice = cameraDevice;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Utils.info(TAG, "onDisconnected");
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            Utils.error(TAG, "CameraDevice error: " + error);
            cameraDevice.close();
            mCameraDevice = null;
            if (mCallback != null) {
                mCallback.onDeviceStateError(error);
            }
        }
    };

    private final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            switch (mState) {
                case PREVIEW:
                    break;
                case WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = State.PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case WAITING_PRECAPTURE: {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = State.WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case WAITING_NON_PRECAPTURE: {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = State.PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };

    private MyCameraController() {}

    private static class Holder {
        private static final MyCameraController INSTANCE = new MyCameraController();
    }

    public static MyCameraController getInstance() {
        return Holder.INSTANCE;
    }

    @Deprecated
    public static MyCameraController newInstance() {
        return getInstance();
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
            String cameraId = null;
            CameraCharacteristics characteristics = null;
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics c = manager.getCameraCharacteristics(id);
                Integer facing = c.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == lensFacing) {
                    cameraId = id;
                    characteristics = c;
                    break;
                }
            }

            if (cameraId == null && manager.getCameraIdList().length > 0) {
                cameraId = manager.getCameraIdList()[0];
                characteristics = manager.getCameraCharacteristics(cameraId);
            }

            if (cameraId == null || characteristics == null) {
                Utils.error(TAG, "No camera found");
                return;
            }

            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) != null ?
                    characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) : 0;

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) return;

            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            
            mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                    ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

            TextureView textureView = getTextureView();
            if (textureView == null) return;

            int viewWidth = textureView.getWidth();
            int viewHeight = textureView.getHeight();
            
            // Determine preview size based on aspect ratio
            int displayRotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
            boolean swappedDimensions = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (mSensorOrientation == 90 || mSensorOrientation == 270) swappedDimensions = true;
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (mSensorOrientation == 0 || mSensorOrientation == 180) swappedDimensions = true;
                    break;
            }

            int rotatedPreviewWidth = swappedDimensions ? viewHeight : viewWidth;
            int rotatedPreviewHeight = swappedDimensions ? viewWidth : viewHeight;
            int maxPreviewWidth = swappedDimensions ? MAX_PREVIEW_HEIGHT : MAX_PREVIEW_WIDTH;
            int maxPreviewHeight = swappedDimensions ? MAX_PREVIEW_WIDTH : MAX_PREVIEW_HEIGHT;

            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                    maxPreviewHeight, largest);

            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mFlashSupported = available != null && available;

            configureTransform(viewWidth, viewHeight, (Activity) context);

            manager.openCamera(cameraId, mDeviceStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Cannot access the camera", e);
        }
    }

    private TextureView getTextureView() {
        return mTextureViewRef != null ? mTextureViewRef.get() : null;
    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            String path = mCallback != null ? mCallback.getPath() : null;
            if (path == null) return;
            
            try (Image image = reader.acquireLatestImage()) {
                if (image == null) return;
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                save(bytes, new File(path));
            } catch (IOException e) {
                Log.e(TAG, "Error saving image", e);
            }
        }

        private void save(byte[] bytes, File file) throws IOException {
            try (OutputStream output = new FileOutputStream(file)) {
                output.write(bytes);
            }
            if (mCallback != null) mCallback.onSaveImageComplete();
        }
    };

    private void startPreview() {
        try {
            TextureView textureView = getTextureView();
            if (textureView == null) return;
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) return;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface surface = new Surface(texture);

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (null == mCameraDevice) return;
                            mCaptureSession = session;
                            try {
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                setAutoFlash(mPreviewRequestBuilder);
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "Error starting preview", e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            if (mCallback != null) mCallback.info("Configuration failed");
                        }
                    }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error starting preview", e);
        }
    }

    public void configureTransform(int viewWidth, int viewHeight, Activity activity) {
        TextureView textureView = getTextureView();
        if (null == textureView || null == mPreviewSize || null == activity) return;

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        float bufferWidth = mPreviewSize.getWidth();
        float bufferHeight = mPreviewSize.getHeight();

        boolean swappedDimensions = false;
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                if (mSensorOrientation == 90 || mSensorOrientation == 270) swappedDimensions = true;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                if (mSensorOrientation == 0 || mSensorOrientation == 180) swappedDimensions = true;
                break;
        }

        float rotatedBufferWidth = swappedDimensions ? bufferHeight : bufferWidth;
        float rotatedBufferHeight = swappedDimensions ? bufferWidth : bufferHeight;

        // Correct for stretching by using Center-Crop logic
        float scaleX = 1.0f;
        float scaleY = 1.0f;
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

    private void captureStillPicture() {
        try {
            if (null == mCameraDevice) return;
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);

            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    if (mCallback != null) mCallback.onCaptureDone();
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error capturing picture", e);
        }
    }

    private void unlockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            mState = State.PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error unlocking focus", e);
        }
    }

    private void runPrecaptureSequence() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = State.WAITING_PRECAPTURE;
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

    public void setPreviewContent(TextureView view) {
        mTextureViewRef = new WeakReference<>(view);
    }

    public void registerCallback(CameraCallback callback) {
        mCallback = callback;
    }

    @Deprecated
    public void registerCallBack(CameraCallback callback) { registerCallback(callback); }

    public void capturePicture() { lockFocus(); }

    private void lockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = State.WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error locking focus", e);
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
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

    @Deprecated
    public interface MyCameraCallBack extends CameraCallback {}

    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}

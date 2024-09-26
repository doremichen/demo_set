package com.adam.app.demoset.camera2.legacy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.adam.app.demoset.Utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;

public class CameraController {

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    private CameraDevice mCameraDevice;

    private Size mPreviewSize;

    private SoftReference<Activity> mRef_act;

    private CaptureRequest.Builder mRequestBuilder;
    private ImageReader mImageReader;

    // CameraUIContent
    private static class CameraUIContent {

        TextureView mTexture;

        public CameraUIContent(TextureView view) {
            this.mTexture = view;
        }
    }

    CameraUIContent mUIContent;

    private CameraController() {
    }

    private static class Singleton {
        public static final CameraController INSTANCE = new CameraController();
    }

    public static CameraController newInstance() {
        return Singleton.INSTANCE;
    }


    public void registerContext(Activity act) {
        this.mRef_act = new SoftReference<>(act);
    }

    public void openCamera(int index, TextureView textView) {
        Utils.info(this, "openCamera enter");
        mUIContent = new CameraUIContent(textView);
        if (ActivityCompat.checkSelfPermission(mRef_act.get(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mRef_act.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mRef_act.get(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, DemoCamera2Act.REQUEST_PERMISSION_CODE);

        } else {
            // Camera service proxy
            CameraManager cameraService = (CameraManager) mRef_act.get().getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = cameraService.getCameraIdList()[index];

                // Set up camera output
                CameraCharacteristics cameraChar = cameraService.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = cameraChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                this.mPreviewSize = map.getOutputSizes(ImageFormat.JPEG)[0];
                int width = (mPreviewSize == null) ? textView.getWidth() : mPreviewSize.getWidth();
                int height = (mPreviewSize == null) ? textView.getHeight() : mPreviewSize.getHeight();
                mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);
                mImageReader.setOnImageAvailableListener((reader) ->{}, mWorkHandler);

                cameraService.openCamera(cameraId, new MyOpenCameraStateCallback(), mWorkHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Start camera work thread
     */
    public void startWorkThread() {
        Utils.info(this, "startWorkThread enter");
        mWorkThread = new HandlerThread("Demo camera2 work task");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
        Utils.info(this, "startWorkThread enter: mWorkHandler = " + mWorkHandler);
    }

    /**
     * Stop camera work thread
     */
    public void stopWorkThread() {
        Utils.info(this, "stopWorkThread enter: mWorkHandler = " + mWorkHandler);
        if (mWorkThread != null) {
            mWorkThread.quitSafely();
            try {
                mWorkThread.join();
                mWorkThread = null;
                mWorkHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void capture() throws IllegalAccessException {
        Utils.info(this, "capture");
        if (mUIContent == null) {
            throw new IllegalAccessException("Please openCamera first!!!");
        }

        ArrayList<Surface> surfaces = new ArrayList<>();
        surfaces.add(new Surface(mUIContent.mTexture.getSurfaceTexture()));
        surfaces.add(this.mImageReader.getSurface());

        try {
            mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mRequestBuilder.addTarget(this.mImageReader.getSurface());
            mRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            this.mCameraDevice.createCaptureSession(surfaces, new MyCaptureStateCallback(), this.mWorkHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void closeCamera() {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.close();
            this.mCameraDevice = null;
        }

        if (this.mImageReader != null) {
            this.mImageReader.close();
            this.mImageReader = null;
        }
    }


    private void startPreview() throws IllegalAccessException {
        Utils.info(this, "startPreview");
        if (mUIContent == null) {
            throw new IllegalAccessException("Please openCamera first!!!");
        }
        // Get Surface
        SurfaceTexture surfaceText = mUIContent.mTexture.getSurfaceTexture();
        surfaceText.setDefaultBufferSize(this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight());
        Surface surface = new Surface((surfaceText));

        // Add surface to camera device
        try {
            mRequestBuilder = this.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mRequestBuilder.addTarget(surface);

            this.mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new MyPreviewStateCallback(), this.mWorkHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private class MyCaptureStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Utils.info(this, "onConfigured enter");

            try {
                session.capture(mRequestBuilder.build(), new MyCaptureCallback(), mWorkHandler);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Utils.info(this, "onConfigureFailed enter");

        }

        private class MyCaptureCallback extends CameraCaptureSession.CaptureCallback {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
                Utils.info(this, "onCaptureStarted");
            }

            @Override
            public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                super.onCaptureProgressed(session, request, partialResult);
                Utils.info(this, "onCaptureProgressed");
            }

            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
                Utils.info(this, "onCaptureFailed");
            }

            @Override
            public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
                super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
                Utils.info(this, "onCaptureSequenceCompleted");
            }

            @Override
            public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
                super.onCaptureSequenceAborted(session, sequenceId);
                Utils.info(this, "onCaptureSequenceAborted");
            }

            @Override
            public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
                super.onCaptureBufferLost(session, request, target, frameNumber);
                Utils.info(this, "onCaptureBufferLost");
            }

            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                Utils.info(this, "onCaptureCompleted");
                // Start preview
                try {
                    startPreview();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class MyOpenCameraStateCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Utils.info(this, "onOpened");
            mCameraDevice = camera;
            // Start preview
            try {
                startPreview();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Utils.info(this, "onDisconnected");
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Utils.info(this, "onError error: " + error);
            camera.close();
            mCameraDevice = null;
        }
    }

    private class MyPreviewStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Utils.info(this, "onConfigured");
            if (mCameraDevice != null) {
                mRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                mRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                try {
                    // just preview so do not add capture callback
                    session.setRepeatingRequest(mRequestBuilder.build(), null, mWorkHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Utils.info(this, "onConfigureFailed");

        }


    }
}

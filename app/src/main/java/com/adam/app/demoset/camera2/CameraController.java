package com.adam.app.demoset.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.adam.app.demoset.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class CameraController {


    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    private CameraDevice mCameraDevice;
    private TextureView mTexture;
    private Size mVewDimension;

    private CaptureRequest.Builder mRequestBuilder;

    private CameraController() {
    }

    private static class Builder {
        public static final CameraController INSTANCE = new CameraController();
    }

    public static CameraController create() {
        return Builder.INSTANCE;
    }


    public void openCamera(Activity act, int index, TextureView textView) {
        Utils.inFo(this, "openCamera enter");
        mTexture = textView;
        WeakReference<Activity> ref_act = new WeakReference<Activity>(act);

        if (ActivityCompat.checkSelfPermission(ref_act.get(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ref_act.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ref_act.get(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, DemoCamera2Act.REQUEST_PERMISSION_CODE);

        } else {
            // Camera service proxy
            CameraManager cameraService = (CameraManager) ref_act.get().getSystemService(Context.CAMERA_SERVICE);
            try {
                String camearId = cameraService.getCameraIdList()[index];

                CameraCharacteristics cameraChar = cameraService.getCameraCharacteristics(camearId);
                StreamConfigurationMap map = cameraChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                this.mVewDimension = map.getOutputSizes(SurfaceTexture.class)[0];
                cameraService.openCamera(camearId, new MyOpenCameraStateCallback(), mWorkHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Start camera work thread
     */
    public void startWorkThread() {
        Utils.inFo(this, "startWorkThread enter");
        mWorkThread = new HandlerThread("Demo camera2 work task");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
        Utils.inFo(this, "startWorkThread enter: mWorkHandler = " + mWorkHandler );
    }

    /**
     * Stop camera work thread
     */
    public void stopWorkThread() {
        Utils.inFo(this, "stopWorkThread enter: mWorkHandler = " + mWorkHandler );
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

    public void capture() {
        Utils.inFo(this, "capture");
        ArrayList<Surface> surfaces = new ArrayList<Surface>();
        surfaces.add(new Surface(mTexture.getSurfaceTexture()));

        try {
            mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mRequestBuilder.addTarget(new Surface(mTexture.getSurfaceTexture()));
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
    }


    private void startPreview() {
        Utils.inFo(this, "startPreview");
        // Get Surface
        SurfaceTexture surfaceText = this.mTexture.getSurfaceTexture();
        surfaceText.setDefaultBufferSize(this.mVewDimension.getWidth(), this.mVewDimension.getHeight());
        Surface surface = new Surface((surfaceText));

        // Add surface to camera device
        try {
            mRequestBuilder = this.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mRequestBuilder.addTarget(surface);

            this.mCameraDevice.createCaptureSession(Arrays.asList(surface), new MyPreviewStateCallback(), this.mWorkHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private class MyCaptureStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Utils.inFo(this, "onConfigured enter");

            try {
                session.capture(mRequestBuilder.build(), new MyCaptureCallback(), mWorkHandler);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Utils.inFo(this, "onConfigureFailed enter");

        }

        private class MyCaptureCallback extends CameraCaptureSession.CaptureCallback {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
                Utils.inFo(this, "onCaptureStarted");
            }

            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                Utils.inFo(this, "onCaptureCompleted");
                // Start preview
                startPreview();
            }
        }
    }

    private class MyOpenCameraStateCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Utils.inFo(this, "onOpened");
            mCameraDevice = camera;
            // Start preview
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Utils.inFo(this, "onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Utils.inFo(this, "onError error: " + error);
        }
    }

    private class MyPreviewStateCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Utils.inFo(this, "onConfigured");
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
            Utils.inFo(this, "onConfigureFailed");
        }


    }
}

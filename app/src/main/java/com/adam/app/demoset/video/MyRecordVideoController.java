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

import com.adam.app.demoset.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyRecordVideoController {

    private HandlerThread mBgThread;
    private Handler mHandler;
    private Size mPreviewSize;
    private MediaRecorder mRecorder;

    private CameraDevice mDevice;

    private CameraDevice.StateCallback mOpenCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Utils.info(this, "onOpened");
            mDevice = camera;

            // start preview
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Utils.info(this, "onDisconnected");
            camera.close();
            mDevice = null;

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Utils.info(this, "onError");
            camera.close();
            mDevice = null;

            // Tell UI
            if (mListener != null) {
                mListener.onError(error);
            }


        }
    };
    private ControllerListener mListener;
    private TextureView mTextureView;
    private CaptureRequest.Builder mRequestBuilder;
    private CameraCaptureSession mPreviewSession;
    private Size mVideoSize;


    private MyRecordVideoController() {
    }

    private static class Helper {
        private static final MyRecordVideoController INSTANCE = new MyRecordVideoController();
    }

    /**
     * Singleton
     *
     * @return
     */
    public static MyRecordVideoController newInstance() {
        return Helper.INSTANCE;
    }

    public void startCameraThread() {
        Utils.info(this, "startCameraThread");
        mBgThread = new HandlerThread("camera work thread");
        mBgThread.start();
        mHandler = new Handler(mBgThread.getLooper());

    }

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

    @SuppressLint("MissingPermission")
    public void openCamera(Context context, TextureView textureView) {
        Utils.info(this, "openCamera");

        mTextureView = textureView;

        CameraManager cameraService = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try {
            // Just use back lens
            String cameraId = cameraService.getCameraIdList()[CameraCharacteristics.LENS_FACING_FRONT];
            // Set up preview size
            CameraCharacteristics cameraChar = cameraService.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));

            // MediaRecorder instance
            mRecorder = new MediaRecorder();

            // Open camera
            cameraService.openCamera(cameraId, mOpenCameraCallback, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
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
    }

    public void startRecord(final Activity activity) {
        Utils.info(this, "startRecord");
        // Check data validity
        if (mDevice == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }

        try {
            // Cloase preview session
            closePreviewSession();
            setUpVideoConfig();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            // Create record request
            mRequestBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            // Set up preview surface to request
            Surface previewSurface = new Surface(texture);
            mRequestBuilder.addTarget(previewSurface);

            // Set up video surface to request
            Surface recordSurface = mRecorder.getSurface();
            mRequestBuilder.addTarget(recordSurface);

            // Prepare surface array list
            ArrayList<Surface> surfaces = new ArrayList<Surface>();
            surfaces.add(previewSurface);
            surfaces.add(recordSurface);

            // Start record session
            mDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Utils.info(this, "onConfigred");
                    mPreviewSession = session;
                    updatePreview();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.info(this, "runOnUiThread...");
                            // Start recording
                            mRecorder.start();
                            Utils.showToast(activity, "Start recording....");
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    // Tell UI
                    if (mListener != null) {
                        mListener.onFail("record config fail");
                    }
                }
            }, mHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    private void setUpVideoConfig() {
        Utils.info(this, "setUpVideoConfig enter");
        if (mRecorder == null || mListener == null) {
            return;
        }

        try {
            // Set up media recorder
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mListener.getPath());
            mRecorder.setVideoEncodingBitRate(10000000);
            mRecorder.setVideoFrameRate(30);
            mRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Utils.info(this, "Recorder onError enter");
                    Utils.info(this, "what = " + what);
                    Utils.info(this, "extra = " + extra);
                }
            });
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        Utils.info(this, "stopRecord");

        mRecorder.setOnErrorListener(null);
        mRecorder.setOnInfoListener(null);

        mRecorder.stop();
        mRecorder.reset();

        if (mListener != null) {
            mListener.onInfo("Stop record...");
        }

        // start preview
        startPreview();
    }

    public void registerListener(ControllerListener listener) {
        mListener = listener;
    }

    private void startPreview() {
        Utils.info(this, "startPreview enter");
        // Check data validity
        if (mDevice == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }

        try {
            // Cloase preview session
            closePreviewSession();

            // Get SurfaceTexture
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            // Config preview request
            mRequestBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            // add preview surface to preview request
            Surface previewSurface = new Surface(surfaceTexture);
            mRequestBuilder.addTarget(previewSurface);

            // Start preview
            mDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    // Tell UI
                    if (mListener != null) {
                        mListener.onFail("Preview config fail");
                    }

                }
            }, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void closePreviewSession() {
        Utils.info(this, "closePreviewSession enter");
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private void updatePreview() {
        Utils.info(this, "updatePreview enter");
        if (mDevice == null) {
            return;
        }

        try {
            mRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            mPreviewSession.setRepeatingRequest(mRequestBuilder.build(), null, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Utils.info(MyRecordVideoController.class, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    interface ControllerListener {
        void onError(int result);

        void onFail(String msg);

        void onInfo(String msg);

        String getPath();
    }

}

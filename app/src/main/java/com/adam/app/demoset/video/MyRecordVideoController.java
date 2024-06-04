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
import java.util.List;

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

    // CaptureRequestStrategy interface
    interface CaptureRequestStrategy {
        void createCaptureRequest() throws CameraAccessException;
        List<Surface> getSurfaces();
    }

    // RecordCaptureRequestStrategy implementation
    class RecordCaptureRequestStrategy implements CaptureRequestStrategy {
        @Override
        public void createCaptureRequest() throws CameraAccessException {
            mRequestBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            // Set up preview and record surfaces
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

    private void startRecordSession(final Activity activity, CaptureRequestStrategy captureRequestStrategy) throws CameraAccessException {
       Utils.info(this, "startRecordSession +++");
        mDevice.createCaptureSession(captureRequestStrategy.getSurfaces(), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                Utils.info(this, "onConfigred");
                mPreviewSession = session;
                updatePreview();

                activity.runOnUiThread(() -> {
                    Utils.info(this, "runOnUiThread...");
                    mRecorder.start();
                    Utils.showToast(activity, "Start recording...");
                });
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                if (mListener != null) {
                    mListener.onFail("record config fail");
                }
            }
        }, mHandler);
        Utils.info(this, "startRecordSession xxx");
    }

    private enum RecordState {
        START,
        STOP;
    }

    private RecordState mRecordState = RecordState.STOP;


    public boolean isRecording() {
        return this.mRecordState == RecordState.START;
    }


    public void startRecord(final Activity activity) {
        Utils.info(this, "startRecord +++");

        // Check data validity
        if (!Utils.areAllNotNull(mDevice, mPreviewSize, mListener)
                || !mTextureView.isAvailable()) {
            Utils.info(this, "check data fail!!!");
            return;
        }

        // check state
        if (isRecording()) {
            mListener.onInfo("Video is recording....");
            return;
        }

        try {
            // Close preview session
            closePreviewSession();

            // Set up video configuration
            setUpVideoConfig();

            // Create capture request using a strategy
            CaptureRequestStrategy captureRequestStrategy = new RecordCaptureRequestStrategy();
            captureRequestStrategy.createCaptureRequest();

            // Start record session
            startRecordSession(activity, captureRequestStrategy);
            // change state
            this.mRecordState = RecordState.START;

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Utils.info(this, "CameraAccessException: " + e.getReason());
        }

        Utils.info(this, "startRecord xxx");
    }

    private void setUpVideoConfig() {
        Utils.info(this, "setUpVideoConfig enter");
        if (!Utils.areAllNotNull(mRecorder, mListener)) {
            Utils.info(this, "check data fail!!!");
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
            mRecorder.setOrientationHint(90);
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

        if (!Utils.areAllNotNull(mRecorder, mListener)) {
            Utils.info(this, "check data fail!!!");
            return;
        }

        // check state
        if (!isRecording()) {
            mListener.onInfo("Video has been stopped!!!");
            return;
        }

        mRecorder.setOnErrorListener(null);
        mRecorder.setOnInfoListener(null);

        mRecorder.stop();
        mRecorder.reset();

        mListener.onInfo("Stop record...");

        // start preview
        startPreview();

        // change state
        this.mRecordState = RecordState.STOP;

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

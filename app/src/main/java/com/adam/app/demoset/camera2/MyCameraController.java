/**
 * Camera controller
 */
package com.adam.app.demoset.camera2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
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
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.adam.app.demoset.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class MyCameraController {

    private static final String THREAD_NAME = "MyCamera work thread";
    private TextureView mView;

    // Camera back ground thread
    private HandlerThread mBgThread;
    private Handler mBgHandler;

    private int mState = STATE.PREVIEW;

    private CameraDevice mDevice;
    private final CameraDevice.StateCallback mDeviceStateCB = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Utils.info(this, "onOpened");
            mDevice = camera;
            // Start camera preview
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mDevice = null;
            if (mCallBack != null) {
                mCallBack.onDeviceStateError(error);
            }
        }
    };
    private MyCameraCallBack mCallBack;
    private Size mPreviewSize;
    private ImageReader mReader;
    private boolean mCanFlash;
    private CaptureRequest.Builder mPreviewReqBuilder;
    private CameraCaptureSession mCaptureSession;

    private final CameraCaptureSession.CaptureCallback mCaptureCallBack = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            Utils.info(this, "process");
            Utils.info(this, "mState = " + mState);
            switch (mState) {
                case STATE.PREVIEW:
                    // do nothing
                    break;
                case STATE.WAITING_LOCK:
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    Utils.info(this, "afState = " + afState);
                    if (afState == null) {
                        // Capture
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        Utils.info(this, "aeState = " + afState);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE.PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPreCaptureSequence();
                        }
                    }
                    break;
                case STATE.WATING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE.WATING_NON_PRECATURE;
                    }
                }
                break;
                case STATE.WATING_NON_PRECATURE: {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE.PICTURE_TAKEN;
                        captureStillPicture();
                    }
                }
                break;
            }
        }

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session,
                                     @NonNull CaptureRequest request,
                                     long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            Utils.info(this, "onCaptureStarted enter");
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            Utils.info(this, "onCaptureProgressed enter");
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Utils.info(this, "onCaptureCompleted enter");
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session,
                                    @NonNull CaptureRequest request,
                                    @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Utils.info(this, "onCaptureFailed enter");
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session,
                                               int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }
    };
    private CaptureRequest mPreviewReq;

    private static final Byte JPEG_QUALITY = 90;

    private MyCameraController() {
    }

    private static class Singleton {
        public static final MyCameraController INSTANCE = new MyCameraController();
    }

    /**
     * Singleton
     *
     * @return MyCameraController
     */
    public static MyCameraController newInstance() {
        return Singleton.INSTANCE;
    }


    public void startCameraThread() {
        Utils.info(this, "startCameraThread enter");
        this.mBgThread = new HandlerThread(THREAD_NAME);
        this.mBgThread.start();
        this.mBgHandler = new Handler(this.mBgThread.getLooper());
    }

    public void stopCameraThread() {
        Utils.info(this, "stopCameraThread enter");
        this.mBgThread.quitSafely();
        try {
            this.mBgThread.join();
            this.mBgThread = null;
            this.mBgHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    public void openCamera(Context context, int id) {
        Utils.info(this, "openCamera enter");
        CameraManager cameraService = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);


        try {
            String cameraId = cameraService.getCameraIdList()[id];

            // Saved file object
            final File file = (mCallBack == null) ? null : new File(mCallBack.getPath());

            // Set up camera output
            CameraCharacteristics CameraChar = cameraService.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = CameraChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            mReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                    ImageFormat.JPEG, 2);
            mReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // Tell UI
                    if (mCallBack != null) {
                        mCallBack.info("image is available...");
                    }

                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        if (file != null) save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }


                }

                private void save(byte[] bytes) throws IOException {
                    Utils.info(this, "save +++");
                    Utils.info(this, "file: " + file.getPath());
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                    // Tell UI
                    if (mCallBack != null) {
                        mCallBack.onSaveImageComplete();
                    }
                    Utils.info(this, "save xxx");
                }
            }, mBgHandler);

            // Check auto flash
            Boolean isFlash = CameraChar.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mCanFlash = isFlash != null;

            // Open camera
            cameraService.openCamera(cameraId, mDeviceStateCB, this.mBgHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Start camera preview
     */
    private void startPreview() {
        Utils.info(this, "startPreview");

        // Create preview session
        SurfaceTexture texture = mView.getSurfaceTexture();
        Utils.info(this, "texture = " + texture);
        Utils.info(this, "mPreviewSize = " + mPreviewSize);
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

        // Preview surface
        Surface surface = new Surface(texture);

        try {
            // Set up CaptureRequest.Singleton
            mPreviewReqBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewReqBuilder.addTarget(surface);

            // Create capture session to display preview
            mDevice.createCaptureSession(Arrays.asList(surface, mReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Utils.info(this, "preview config: onConfigured enter");
                    // When the session is ready, start to displaying the preview
                    mCaptureSession = session;

                    // Auto focus mode
                    mPreviewReqBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                    // Flash mode
                    if (mCanFlash) {
                        mPreviewReqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    }

                    try {
                        // Start to display the preview
                        mPreviewReq = mPreviewReqBuilder.build();
                        session.setRepeatingRequest(mPreviewReq, mCaptureCallBack, mBgHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Utils.info(this, "preview config: onConfigureFailed enter");
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Utils.info(this, "reason: " + e.getReason());
        }

    }


    /** Zero weight 3A region, to reset regions per API. */
    private static final MeteringRectangle[] ZERO_WEIGHT_3A_REGION = new MeteringRectangle[]{
            new MeteringRectangle(0, 0, 0, 0, 0)
    };

    /**
     * Calculates sensor crop region for a zoom level (zoom >= 1.0).
     *
     * @return Crop region.
     */
    private Rect cropRegionForZoom(CameraCharacteristics characteristics, float zoom) {
        Rect sensor = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        int xCenter = sensor.width() / 2;
        int yCenter = sensor.height() / 2;
        int xDelta = (int) (0.5f * sensor.width() / zoom);
        int yDelta = (int) (0.5f * sensor.height() / zoom);
        return new Rect(xCenter - xDelta, yCenter - yDelta, xCenter + xDelta, yCenter + yDelta);
    }
    /**
     * Adds current regions to CaptureRequest and base AF mode +
     * AF_TRIGGER_IDLE.
     *
     * @param builder Build for the CaptureRequest
     */
    private void addBaselineCaptureKeysToRequest(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AF_REGIONS, ZERO_WEIGHT_3A_REGION);
        builder.set(CaptureRequest.CONTROL_AE_REGIONS, ZERO_WEIGHT_3A_REGION);
        builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
        // Enable face detection
        builder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,
                CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL);
        builder.set(CaptureRequest.CONTROL_SCENE_MODE,
                CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);
    }
    private void captureStillPicture() {
        Utils.info(this, "captureStillPicture enter");
        try {
            // Take a picture request
            final CaptureRequest.Builder builder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            addBaselineCaptureKeysToRequest(builder);
            builder.set(CaptureRequest.JPEG_QUALITY, JPEG_QUALITY);
            builder.set(CaptureRequest.JPEG_ORIENTATION, 90);
            builder.addTarget(mReader.getSurface());

            // set AF mode
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            // set flash
            if (mCanFlash) {
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }

            // Take capture session call back
            CameraCaptureSession.CaptureCallback captureCallBack = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Utils.info(this, "captureCallBack: onCaptureCompleted");
                    // Tell UI
                    if (mCallBack != null) {
                        mCallBack.onCaptureDone();
                    }
                    restartPreview();
                }

            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(builder.build(), captureCallBack, null);

        } catch (CameraAccessException e) {
            Utils.info(this, "reason: "+ e.getReason());
            e.printStackTrace();
        }
    }

    private void runPreCaptureSequence() {
        Utils.info(this, "runPreCaptureSequence");
        try {
            // This is how to tell the camera to trigger.
            mPreviewReqBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the preCapture sequence to be set.
            mState = STATE.WATING_PRECAPTURE;
            mCaptureSession.capture(mPreviewReqBuilder.build(), mCaptureCallBack,
                    mBgHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void restartPreview() {
        Utils.info(this, "restartPreview");
        try {
            mPreviewReqBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            if (mCanFlash) {
                mPreviewReqBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }
            mCaptureSession.capture(mPreviewReqBuilder.build(), mCaptureCallBack, mBgHandler);
            mState = STATE.PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewReq, mCaptureCallBack, mBgHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close camera by CameraDevice
     */
    public void closeCamera() {
        Utils.info(this, "closeCamera enter");
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }

        if (this.mDevice != null) {
            this.mDevice.close();
            this.mDevice = null;
        }

        if (mReader != null) {
            mReader.close();
            mReader = null;
        }
    }


    /**
     * Get view from UI
     *
     * @param view:
     */
    public void setPreviewContent(TextureView view) {
        WeakReference<TextureView> weakRef = new WeakReference<>(view);
        mView = weakRef.get();
    }

    public void registerCallBack(MyCameraCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * Take a picture
     */
    public void capturePicture() {
        Utils.info(this, "capturePicture");

        try {
            // This is how to tell the camera to lock focus.
            mPreviewReqBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell mCaptureCallBack to waiting lock state.
            mState = STATE.WAITING_LOCK;
            mCaptureSession.capture(mPreviewReqBuilder.build(), mCaptureCallBack, mBgHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * CallBack to UI
     */
    public interface MyCameraCallBack {
        void onCaptureDone();

        void info(String str);

        void onDeviceStateError(int code);

        String getPath();

        void onSaveImageComplete();
    }

    /**
     * Camera state
     */
    private interface STATE {
        int PREVIEW = 0;
        int WAITING_LOCK = 1;
        int WATING_PRECAPTURE = 2;
        int WATING_NON_PRECATURE = 3;
        int PICTURE_TAKEN = 4;
    }

}

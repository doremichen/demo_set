/**
 * Camera demo
 */
package com.adam.app.demoset.camera2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.io.File;

public class DemoCamera2Act2 extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x1357;
    private int mIndex;
    private boolean mCanOpenCamera;

    private MyCameraController mCameraController;

    private TextureView mView;

    private String mFilePath;

    private boolean mCaptureDone;

    /**
     * Callback from MyCameraController
     */
    private MyCameraController.MyCameraCallBack mDeviceSateCallBack = new MyCameraController.MyCameraCallBack() {
        @Override
        public void onCaptureDone() {
            Utils.showToast(DemoCamera2Act2.this, "Capture Done!!!");
            mCaptureDone = true;
        }

        @Override
        public void info(String str) {
            Utils.showToast(DemoCamera2Act2.this, str);
        }

        @Override
        public void onDeviceStateError(int code) {
            // Finish UI
            DemoCamera2Act2.this.finish();
        }

        @Override
        public String getPath() {
            File fileDir = DemoCamera2Act2.this.getFilesDir();
            mFilePath = fileDir.getPath() + "/" +
                    System.currentTimeMillis() + ".jpg";
            return mFilePath;
        }
    };


    /**
     * Surface view listener
     */
    private TextureView.SurfaceTextureListener mTextureViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureAvailable enter");
            mCameraController.openCamera(DemoCamera2Act2.this, mIndex);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };


    //camera  permission
    private static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_camera2_act2);

        this.mView = this.findViewById(R.id.textureView_act2);

        // MyCamera controller
        mCameraController = MyCameraController.newInstance();

        mCameraController.setPreviewContent(mView);
        mCameraController.registerCallBack(mDeviceSateCallBack);

        // Ask camera permission
        if (Utils.askPermission(this, CAMERA_PERMISSION, REQUEST_CAMERA_PERMISSION_CODE)) {
            mCanOpenCamera = true;
        }

        //Start camera work thread
        mCameraController.startCameraThread();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu_camera, menu);

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.info(this, "onResume enter");
        // Check permission and cameraDevice
        if (mCanOpenCamera) {
            if (mView.isAvailable()) {
                // Open camera
                mCameraController.openCamera(this, mIndex);
            } else {
                this.mView.setSurfaceTextureListener(mTextureViewListener);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Utils.info(this, "onPause");
        if (mCanOpenCamera) {
            // Close camera
            mCameraController.closeCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop my camera work thread
        mCameraController.stopCameraThread();
    }

    /**
     * The callback of menu item is pressed.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.front_lens:
                switchToFrontCamera(true);
                return true;
            case R.id.rear_lens:
                switchToFrontCamera(false);
                return true;
            case R.id.exit_camera:
                this.finish();
                return true;
        }
        return false;
    }

    /**
     * Receive the result of permission request.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length == CAMERA_PERMISSION.length) {
                for (int result : grantResults) {
                    Utils.info(this, "result = " + result);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Utils.showToast(this, "Camera permission is not granted");
                        mCanOpenCamera = false;
                        // finish UI
                        DemoCamera2Act2.this.finish();
                        break;
                    } else {
                        Utils.showToast(this, "Camera permission is granted");
                        mCanOpenCamera = true;
                        // Open camera
                        mCameraController.openCamera(this, mIndex);
                    }
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    /**
     * Take picture button
     * @param v
     */
    public void onTakePic(View v) {
        Utils.info(this, "onTakePic enter");
        mCameraController.capturePicture();
    }

    /**
     * Show result button
     * @param view
     */
    public void onResult(View view) {
        Utils.info(this, "onResult enter");
        if (mCaptureDone) {
            // Start play video app
            Intent intent = playImageIntent();
            this.startActivity(intent);
            mCaptureDone = false;
        } else {
            Utils.showToast(this, "No result!!!");
        }

    }

    private Intent playImageIntent() {
        Utils.info(this, "playVedioIntent");
        Utils.info(this, "mFilePath = " + mFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check file exists
        File file = new File(mFilePath);
        if (!file.exists()) {
            Utils.showToast(this, "No this file");
        } else {
            Uri contentUri = FileProvider.getUriForFile(this, "com.adam.app.demoset.fileprovider", file);
            Utils.showToast(this, "<content>" + contentUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "image/*");

        }
        return intent;
    }

    // ----------------------------------------------------
    // private method
    /**
     * Switch the front and back camera by flag.
     * @param isFront true front camera
     *                false back camera
     */
    private void switchToFrontCamera(boolean isFront) {
        Utils.info(this, "switchToFrontCamera: isFront = " + isFront);
        mIndex = (isFront == true)? CameraCharacteristics.LENS_FACING_BACK: CameraCharacteristics.LENS_FACING_FRONT;
        // Close camera
        mCameraController.closeCamera();
        // Open camera
        mCameraController.openCamera(this, mIndex);
    }

}

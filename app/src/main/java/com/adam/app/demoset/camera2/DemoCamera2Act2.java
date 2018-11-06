package com.adam.app.demoset.camera2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoCamera2Act2 extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x1357;
    private int mIndex;
    private boolean mCanOpenCamera;

    private MyCameraController mCameraController;

    private TextureView mView;


    private MyCameraController.MyCameraCallBack mDeviceSateCallBack = new MyCameraController.MyCameraCallBack() {
        @Override
        public void onCaptureDone() {
            Utils.showToast(DemoCamera2Act2.this, "Capture Done...");
        }

        @Override
        public void onDeviceStateError(int code) {
            // Finish UI
            DemoCamera2Act2.this.finish();
        }
    };


    private TextureView.SurfaceTextureListener mTextureViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Utils.inFo(this, "onSurfaceTextureAvailable enter");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_camera2_act2);

        this.mView = this.findViewById(R.id.textureView_act2);

        // Mycamera controller
        mCameraController = MyCameraController.newInstance();

        mCameraController.setPreviewContent(mView);
        mCameraController.registerCallBack(mDeviceSateCallBack);

        // Ask camera permission
        if (Utils.askPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION_CODE)) {
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
        Utils.inFo(this, "onResume enter");
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
        Utils.inFo(this, "onPause");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.front_lens:
                mIndex = CameraCharacteristics.LENS_FACING_BACK;
                // Close camera
                mCameraController.closeCamera();
                // Open camera
                mCameraController.openCamera(this, mIndex);
                return true;
            case R.id.rear_lens:
                mIndex = CameraCharacteristics.LENS_FACING_FRONT;
                // Close camera
                mCameraController.closeCamera();
                // Open camera
                mCameraController.openCamera(this, mIndex);
                return true;
            case R.id.exit_camera:
                this.finish();
                return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Utils.showToast(this, "Camera permission is not granted");
                mCanOpenCamera = false;
                // finish UI
                DemoCamera2Act2.this.finish();
            } else {
                Utils.showToast(this, "Camera permission is granted");
                mCanOpenCamera = true;
                // Open camera
                mCameraController.openCamera(this, mIndex);
            }
        }

    }

    public void onTakePic(View v) {
        Utils.inFo(this, "onTakePic enter");
        mCameraController.capturePicture();
    }
}

package com.adam.app.demoset.camera2.legacy;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoCamera2Act extends AppCompatActivity {

    public static final int REQUEST_PERMISSION_CODE = 0x1357;


    private TextureView mSureView;
    private MySurfaceTextureListener mTextureListener;

    // Control lens
    private int mIndex;
    private CameraController mCameraController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");
        setContentView(R.layout.activity_demo_camera2);

        mSureView = this.findViewById(R.id.textureView_act2);

        // register surface listener
        mTextureListener = new MySurfaceTextureListener();
        mSureView.setSurfaceTextureListener(mTextureListener);

        // Get camera controller
        mCameraController = CameraController.newInstance();
        mCameraController.registerContext(this);

        // Start work thread
        mCameraController.startWorkThread();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.info(this, "onResume enter");
        // open camera
        if (this.mSureView.isAvailable()) {
            mCameraController.openCamera(mIndex, this.mSureView);
        } else {
            this.mSureView.setSurfaceTextureListener(mTextureListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.info(this, "onPause enter");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy enter");
        // close camera
        mCameraController.stopWorkThread();
        mCameraController.closeCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu_camera, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.front_lens:
                mIndex = CameraCharacteristics.LENS_FACING_BACK;
                // Close camera
                mCameraController.closeCamera();
                // Open camera
                mCameraController.openCamera(mIndex, this.mSureView);
                return true;
            case R.id.rear_lens:
                mIndex = CameraCharacteristics.LENS_FACING_FRONT;
                // Close camera
                mCameraController.closeCamera();
                // Open camera
                mCameraController.openCamera(mIndex, this.mSureView);
                return true;
            case R.id.exit_camera:
                this.finish();
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Deny permission finish activity
                this.finish();
            }
        }
    }

    /**
     * Take picture button callback
     *
     * @param view:
     */
    public void onTakePic(View view) {
        Utils.info(this, "onTakePic enter");
        try {
            mCameraController.capture();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    private class MySurfaceTextureListener implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureAvailable enter");
            mCameraController.openCamera(0, mSureView);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureSizeChanged enter");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Utils.info(this, "onSurfaceTextureDestroyed enter");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            Utils.inFo(this, "onSurfaceTextureUpdated enter");

        }
    }
}

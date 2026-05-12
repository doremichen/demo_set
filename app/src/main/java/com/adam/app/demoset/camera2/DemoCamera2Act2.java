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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoCamera2Act2Binding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.io.File;

/**
 * Demo Camera2 Activity.
 * Cleaned up and added transformation logic to fix preview stretching.
 */
public class DemoCamera2Act2 extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x1357;
    private static final String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA};

    private int mLensFacing = CameraCharacteristics.LENS_FACING_BACK;
    private boolean mCanOpenCamera;
    private MyCameraController mCameraController;
    private String mFilePath;
    private boolean mCaptureDone;

    private ActivityDemoCamera2Act2Binding mBinding;

    private final TextureView.SurfaceTextureListener mTextureViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureAvailable: " + width + "x" + height);
            if (mCanOpenCamera) {
                mCameraController.openCamera(DemoCamera2Act2.this, mLensFacing);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureSizeChanged: " + width + "x" + height);
            mCameraController.configureTransform(width, height, DemoCamera2Act2.this);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        }
    };

    private final MyCameraController.CameraCallback mCameraCallback = new MyCameraController.CameraCallback() {
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
            Utils.showToast(DemoCamera2Act2.this, "Device state error: " + code);
            finish();
        }

        @Override
        public String getPath() {
            File outputDir = new File(getFilesDir(), "images");
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                Utils.error(this, "Failed to create images directory");
            }
            File outputFile = new File(outputDir, System.currentTimeMillis() + ".jpg");
            mFilePath = outputFile.getAbsolutePath();
            return mFilePath;
        }

        @Override
        public void onSaveImageComplete() {
            Utils.showToast(DemoCamera2Act2.this, "Image saved successfully!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        mBinding = ActivityDemoCamera2Act2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        UIUtils.hideSystemBar(getWindow());

        mCameraController = MyCameraController.getInstance();
        mCameraController.setPreviewContent(mBinding.textureViewAct2);
        mCameraController.registerCallback(mCameraCallback);

        if (Utils.askPermission(this, CAMERA_PERMISSION, REQUEST_CAMERA_PERMISSION_CODE)) {
            mCanOpenCamera = true;
            mCameraController.startCameraThread();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_camera, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCanOpenCamera) {
            if (mBinding.textureViewAct2.isAvailable()) {
                mCameraController.openCamera(this, mLensFacing);
            } else {
                mBinding.textureViewAct2.setSurfaceTextureListener(mTextureViewListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCanOpenCamera) {
            mCameraController.closeCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraController.stopCameraThread();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.front_lens) {
            switchToFrontCamera(true);
            return true;
        } else if (itemId == R.id.rear_lens) {
            switchToFrontCamera(false);
            return true;
        } else if (itemId == R.id.exit_camera) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCanOpenCamera = true;
                mCameraController.startCameraThread();
                if (mBinding.textureViewAct2.isAvailable()) {
                    mCameraController.openCamera(this, mLensFacing);
                } else {
                    mBinding.textureViewAct2.setSurfaceTextureListener(mTextureViewListener);
                }
            } else {
                Utils.showToast(this, "Camera permission is required");
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onTakePic(View v) {
        mCameraController.capturePicture();
    }

    public void onResult(View view) {
        if (mCaptureDone && mFilePath != null) {
            startActivity(playImageIntent());
            mCaptureDone = false;
        } else {
            Utils.showToast(this, "No image captured yet!");
        }
    }

    private Intent playImageIntent() {
        File file = new File(mFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (file.exists()) {
            Uri contentUri = FileProvider.getUriForFile(this,
                    "com.adam.app.demoset.filemanager.provider", file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "image/*");
        } else {
            Utils.showToast(this, "File not found");
        }
        return intent;
    }

    private void switchToFrontCamera(boolean isFront) {
        mLensFacing = isFront ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
        mCameraController.closeCamera();
        if (mBinding.textureViewAct2.isAvailable()) {
            mCameraController.openCamera(this, mLensFacing);
        }
    }
}

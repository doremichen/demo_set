/*
 * Copyright (c) 2026 Adam Chen
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.camera2.controller.MyCameraController;
import com.adam.app.demoset.camera2.viewmodel.Camera2ViewModel;
import com.adam.app.demoset.databinding.ActivityDemoCamera2Act2Binding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.io.File;

/**
 * Demo Camera2 Activity.
 */
public class DemoCamera2Act2 extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x1357;
    private static final String[] CAMERA_PERMISSION = {Manifest.permission.CAMERA};
    public static final String URI_AUTHORITY = "com.adam.app.demoset.filemanager.provider";

    private boolean mCanOpenCamera;
    private Camera2ViewModel mViewModel;
    private ActivityDemoCamera2Act2Binding mBinding;

    private final TextureView.SurfaceTextureListener mTextureViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            if (mCanOpenCamera) {
                Integer facing = mViewModel.getLensFacing().getValue();
                mViewModel.openCamera(DemoCamera2Act2.this, facing != null ? facing : CameraCharacteristics.LENS_FACING_BACK);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            MyCameraController.getInstance().configureTransform(width, height, DemoCamera2Act2.this);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) { return true; }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_camera2_act2);
        mViewModel = new ViewModelProvider(this).get(Camera2ViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        UIUtils.hideSystemBar(getWindow());
        MyCameraController.getInstance().setPreviewContent(mBinding.textureViewAct2);

        if (Utils.askPermission(this, CAMERA_PERMISSION, REQUEST_CAMERA_PERMISSION_CODE)) {
            mCanOpenCamera = true;
            mViewModel.startCameraThread();
        }

        observeViewModel();
    }

    private void observeViewModel() {
        mViewModel.getToastEvent().observe(this, resId -> {
            if (resId != null) Utils.showToast(this, getString(resId));
        });

        mViewModel.getErrorEvent().observe(this, code -> {
            if (code != null) {
                Utils.showToast(this, "Device state error: " + code);
                finish();
            }
        });

        mViewModel.getShowResultRequest().observe(this, trigger -> {
            if (Boolean.TRUE.equals(trigger)) {
                onResult();
                mViewModel.resetShowResultRequest();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_camera, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mCanOpenCamera) return;
        if (mBinding.textureViewAct2.isAvailable()) {
            Integer facing = mViewModel.getLensFacing().getValue();
            mViewModel.openCamera(this, facing != null ? facing : CameraCharacteristics.LENS_FACING_BACK);
        } else {
            mBinding.textureViewAct2.setSurfaceTextureListener(mTextureViewListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCanOpenCamera) mViewModel.closeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopCameraThread();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CameraMenuStrategy strategy = CameraMenuStrategy.fromId(item.getItemId());
        if (strategy != null) {
            strategy.execute(this, mViewModel);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCanOpenCamera = true;
            mViewModel.startCameraThread();
            onResume();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onResult() {
        if (Boolean.FALSE.equals(mViewModel.getCaptureDone().getValue())) {
            Utils.showToast(this, getString(R.string.demo_camera2_no_image_captured_yet_toast));
            return;
        }

        Intent intent = playImageIntent();
        if (intent != null) {
            startActivity(intent);
            mViewModel.resetCaptureDone();
        } else {
            final String msg = getString(R.string.demo_camera2_image_saved_exception_info);
            Utils.showAlertDialog(this, R.string.dialog_info, msg,
                    new Utils.DialogButton(getString(R.string.label_ok_btn), (dialog, which) -> dialog.dismiss()));
        }
    }

    private Intent playImageIntent() {
        String path = mViewModel.getFilePath().getValue();
        if (path == null) return null;
        File file = new File(path);
        if (!file.exists()) return null;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = FileProvider.getUriForFile(this, URI_AUTHORITY, file);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(contentUri, "image/*");
        return intent;
    }

    /**
     * Enum Strategy Pattern for Menu Items.
     */
    private enum CameraMenuStrategy {
        FRONT_LENS(R.id.front_lens) {
            @Override void execute(DemoCamera2Act2 activity, Camera2ViewModel viewModel) { viewModel.switchCamera(activity, true); }
        },
        REAR_LENS(R.id.rear_lens) {
            @Override void execute(DemoCamera2Act2 activity, Camera2ViewModel viewModel) { viewModel.switchCamera(activity, false); }
        },
        EXIT_CAMERA(R.id.exit_camera) {
            @Override void execute(DemoCamera2Act2 activity, Camera2ViewModel viewModel) { activity.finish(); }
        };

        private final int id;
        CameraMenuStrategy(int id) { this.id = id; }
        abstract void execute(DemoCamera2Act2 activity, Camera2ViewModel viewModel);
        static CameraMenuStrategy fromId(int id) {
            for (CameraMenuStrategy strategy : values()) { if (strategy.id == id) return strategy; }
            return null;
        }
    }
}

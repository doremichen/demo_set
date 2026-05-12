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

package com.adam.app.demoset.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.camera.viewmodel.CameraXViewModel;
import com.adam.app.demoset.databinding.ActivityDemoCameraxBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

/**
 * Activity for CameraX Demo.
 * Driven by ViewModel state. Observes state changes to manage hardware bindings.
 */
public class CameraXActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    private ActivityDemoCameraxBinding mBinding;
    private CameraXViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_camerax);
        mViewModel = new ViewModelProvider(this).get(CameraXViewModel.class);

        mBinding.setVm(mViewModel);
        mBinding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        UIUtils.hideSystemBar(getWindow());

        // Setup observers first
        observeViewModel();

        // Check permissions. Binding will be triggered by LiveData observation if granted.
        if (!allPermissionsGranted()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void initCamera() {
        if (allPermissionsGranted()) {
            mViewModel.initializeCamera(this, mBinding.viewFinder.getSurfaceProvider());
        }
    }

    private void observeViewModel() {
        // Photo viewing event
        mViewModel.getViewPhotoEvent().observe(this, trigger -> {
            if (Boolean.TRUE.equals(trigger)) {
                viewLastPhoto(mViewModel.getLastPhotoUri().getValue());
                mViewModel.onViewPhotoHandled();
            }
        });

        // State-driven camera binding: Re-bind whenever lens facing changes
        mViewModel.getLensFacing().observe(this, facing -> {
            initCamera();
        });
    }

    private void viewLastPhoto(Uri uri) {
        if (uri == null) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Utils.showToast(this, "No app found to view image");
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initCamera();
            } else {
                Utils.showToast(this, getString(R.string.msg_camerax_permission_required));
                finish();
            }
        }
    }
}

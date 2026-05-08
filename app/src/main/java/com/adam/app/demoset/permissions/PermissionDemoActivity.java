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

package com.adam.app.demoset.permissions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityPermissionDemoBinding;
import com.adam.app.demoset.permissions.viewmodel.PermissionViewModel;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.util.Map;

/**
 * Demo activity for Modern Permission Model using MVVM and DataBinding.
 */
public class PermissionDemoActivity extends AppCompatActivity implements PermissionViewModel.PermissionRequester {

    private PermissionViewModel mViewModel;

    // Register ActivityResultLauncher for single permission
    private final ActivityResultLauncher<String> mRequestSinglePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->
                mViewModel.handlePermissionResult(android.Manifest.permission.CAMERA, isGranted));

    // Register ActivityResultLauncher for multiple permissions
    private final ActivityResultLauncher<String[]> mRequestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                StringBuilder status = new StringBuilder();
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    String permission = entry.getKey();
                    boolean isGranted = entry.getValue();
                    status.append(permission).append(": ").append(isGranted ? "GRANTED" : "DENIED").append("\n");
                }
                mViewModel.updateStatus(status.toString().trim());
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPermissionDemoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_permission_demo);

        mViewModel = new ViewModelProvider(this).get(PermissionViewModel.class);
        mViewModel.setRequester(this);
        
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);

        binding.btnExit.setOnClickListener(v -> finish());
    }

    @Override
    public void onLaunchSinglePermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Show rationale dialog
            Utils.DialogButton okBtn = new Utils.DialogButton(getString(R.string.label_ok_btn),
                    (dialog, which) -> mRequestSinglePermissionLauncher.launch(permission));
            Utils.showAlertDialog(this, 
                    R.string.demo_permission_instruction_title,
                    R.string.msg_permission_rationale, 
                    okBtn);
        } else {
            mRequestSinglePermissionLauncher.launch(permission);
        }
    }

    @Override
    public void onLaunchMultiplePermissions(String[] permissions) {
        mRequestMultiplePermissionsLauncher.launch(permissions);
    }

    @Override
    public void onHandleDenied(String permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Permission permanently denied
            mViewModel.updateStatus(getString(R.string.msg_permission_permanently_denied));
            showSettingsDialog();
        } else {
            mViewModel.updateStatus(getString(R.string.msg_permission_denied, permission));
        }
    }

    private void showSettingsDialog() {
        Utils.DialogButton settingBtn = new Utils.DialogButton(getString(R.string.label_setting_btn),
                (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                });
        Utils.showAlertDialog(this, 
                R.string.demo_permission_instruction_title,
                R.string.msg_permission_permanently_denied, 
                settingBtn);
    }
}

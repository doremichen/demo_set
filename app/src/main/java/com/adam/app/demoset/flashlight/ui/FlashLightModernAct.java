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

package com.adam.app.demoset.flashlight.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkInfo;
import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoFlashLightBinding;
import com.adam.app.demoset.flashlight.data.FlashLightMetadata;
import com.adam.app.demoset.flashlight.viewmodel.ModernViewModel;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

/**
 * Modern Flashlight Activity
 * Uses WorkManager for hardware control.
 */
public class FlashLightModernAct extends AppCompatActivity {

    private ModernViewModel mViewModel;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Utils.showToast(this, getString(R.string.flashlight_permission_denied));
                    this.finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDemoFlashLightBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_flash_light);
        binding.setLifecycleOwner(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setTitle(R.string.flashlight_act_title_modern);

        UIUtils.applySystemBarInsets(binding.getRoot(), binding.appBarWrapper);

        mViewModel = new ViewModelProvider(this).get(ModernViewModel.class);

        boolean isFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);

        mViewModel.getWorkInfos().observe(this, workInfos -> {
            if (workInfos != null && !workInfos.isEmpty()) {
                WorkInfo workInfo = workInfos.get(0);
                binding.tvWorkStatus.setText(workInfo.getState().name());
            }
        });

        if (isFlash) {
            String status = System.getProperty(FlashLightMetadata.PROP_FLASH_LIGHT_ENABLE);
            if (FlashLightMetadata.CMD_FLASH_LIGHT_ON.equals(status)) {
                binding.switchFlashlight.setChecked(true);
            }

            binding.switchFlashlight.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String action = (isChecked) ? FlashLightMetadata.CMD_FLASH_LIGHT_ON : FlashLightMetadata.CMD_FLASH_LIGHT_OFF;
                System.setProperty(FlashLightMetadata.PROP_FLASH_LIGHT_ENABLE, action);
                mViewModel.toggleFlashlight(isChecked);
            });
        }
    }
}

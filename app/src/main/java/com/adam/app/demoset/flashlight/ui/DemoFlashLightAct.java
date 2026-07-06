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
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkInfo;

import com.adam.app.demoset.R;
import com.adam.app.demoset.flashlight.data.FlashLightMetadata;
import com.adam.app.demoset.flashlight.domain.FlashLightService;
import com.adam.app.demoset.flashlight.viewmodel.FlashLightViewModel;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoFlashLightBinding;
import com.adam.app.demoset.utils.UIUtils;

public class DemoFlashLightAct extends AppCompatActivity {

    private static final boolean USE_SERVICE = true;
    private FlashLightViewModel mFlViewModel;

    private final ActivityResultLauncher<String> mRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Utils.showToast(this, "Permission granted");
                } else {
                    Utils.showToast(this, "Permission not granted");
                    this.finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDemoFlashLightBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_flash_light);
        binding.setLifecycleOwner(this);

        UIUtils.applySystemBarInsets(binding.getRoot(), binding.appBarWrapper);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        this.mFlViewModel = new ViewModelProvider(this).get(FlashLightViewModel.class);
        binding.setViewModel(mFlViewModel);

        boolean isFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        mRequestPermissionLauncher.launch(Manifest.permission.CAMERA);

        this.mFlViewModel.getWorkInfos().observe(this, workInfos -> {
            if (workInfos != null && !workInfos.isEmpty()) {
                WorkInfo workInfo = workInfos.get(0);
                String status = workInfo.getState().name();
                binding.tvWorkStatus.setText(status);
                if (workInfo.getState().isFinished()) {
                    Utils.info(this, "onUpdate");
                }
            }
        });

        if (isFlash) {
            Utils.showToast(this, "the flash light is available");

            String status = System.getProperty(FlashLightMetadata.PROP_FLASH_LIGHT_ENABLE);
            if (FlashLightMetadata.CMD_FLASH_LIGHT_ON.equals(status)) {
                binding.switchFlashlight.setChecked(true);
            }

            binding.switchFlashlight.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String action = (isChecked) ? FlashLightMetadata.CMD_FLASH_LIGHT_ON : FlashLightMetadata.CMD_FLASH_LIGHT_OFF;
                Utils.showToast(DemoFlashLightAct.this, "action: " + action);
                System.setProperty(FlashLightMetadata.PROP_FLASH_LIGHT_ENABLE, action);
                
                if (USE_SERVICE) {
                    FlashLightService.execute(DemoFlashLightAct.this, action);
                } else {
                    mFlViewModel.enableFlashlight(isChecked);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.demo_exit) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

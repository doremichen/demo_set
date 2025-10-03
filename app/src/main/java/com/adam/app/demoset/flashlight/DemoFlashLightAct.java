/**
 * Flash light demo
 */
package com.adam.app.demoset.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoFlashLightBinding;

public class DemoFlashLightAct extends AppCompatActivity implements FlashLightViewModel.ViewModelCallBack {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    public static final String PROP_FLASH_LIGHT_ENABLE = "flash light enable";
    private static final boolean USE_SERVICE = true;
    // flash light view model
    private FlashLightViewModel mFlViewModel;
    // view binding
    private ActivityDemoFlashLightBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoFlashLightBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // check flash light
        boolean isFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        // Ask permission
        Utils.askPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION_CODE);

        // instance view model
        this.mFlViewModel = new FlashLightViewModel(this, this);

        if (isFlash) {
            Utils.showToast(this, "the flash light is available");

            // Check flash light status
            String status = System.getProperty(PROP_FLASH_LIGHT_ENABLE);

            if (FlashLightService.CMD_FLASH_LIGHT_ON.equals(status)) {
                // enable flash in switch
                mBinding.switchFlashlight.setChecked(true);
            }

            // set switch listener
            mBinding.switchFlashlight.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String action = (isChecked) ? FlashLightService.CMD_FLASH_LIGHT_ON : FlashLightService.CMD_FLASH_LIGHT_OFF;
                Utils.showToast(DemoFlashLightAct.this, "action: " + action);
                System.setProperty(PROP_FLASH_LIGHT_ENABLE, action);
                // enable flash
                if (USE_SERVICE) {
                    FlashLightService.execute(DemoFlashLightAct.this, action);
                } else {
                    mFlViewModel.enableFlashlight(isChecked);
                }
            });


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showToast(this, "Permission granted");
            } else {
                Utils.showToast(this, "Permission not granted");
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                this.finish();
                return true;
        }

        return false;
    }

    @Override
    public void onUpdate() {
        Utils.info(this, "onUpdate");
    }
}

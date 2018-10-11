package com.adam.app.demoset.flashlight;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoFlashLightAct extends AppCompatActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    public static final String PROP_FLASH_LIGHT_ENABLE = "flash light enable";
    public static final String FLASH_ON = "on";
    public static final String FLASH_OFF = "off";
    private ToggleButton mTButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_flash_light);

        boolean isFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        Utils.askPermission(this, Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION_CODE);

        if (isFlash) {
            Utils.showToast(this, "the flash light is available");

            mTButton = (ToggleButton) this.findViewById(R.id.toggleButton_flashlight);

            // Check flash light status
            String status = System.getProperty(PROP_FLASH_LIGHT_ENABLE);

            if (FLASH_ON.equals(status)) {
                mTButton.setChecked(true);
            }

            mTButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTButton.isChecked()) {
                        Utils.showToast(DemoFlashLightAct.this, "flash light on...");
                        System.setProperty(PROP_FLASH_LIGHT_ENABLE, FLASH_ON);
                        FlashLightService.execute(DemoFlashLightAct.this, FlashLightService.CMD_FLASH_LIGHT_ON);


                    } else {
                        Utils.showToast(DemoFlashLightAct.this, "flash light off...");
                        System.setProperty(PROP_FLASH_LIGHT_ENABLE, FLASH_OFF);
                        FlashLightService.execute(DemoFlashLightAct.this, FlashLightService.CMD_FLASH_LIGHT_OFF);

                    }
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
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_bt_exit:
                this.finish();
                return true;
        }

        return false;
    }
}

package com.adam.app.demoset.floatingwindow;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

/**
 * UI interface of the demo floating
 */
public class DemoFloatingWindowAct extends AppCompatActivity {

    private static final int REQUEST_ALTER_WINDOW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_floating_window);
        // check overlay setting enable or not
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            this.startActivityForResult(intent, REQUEST_ALTER_WINDOW);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == REQUEST_ALTER_WINDOW) {
            Utils.info(this, "onActivityResult");
            if (!Settings.canDrawOverlays(this)) {
                Utils.showToast(this, "Permission is not been granted yet");
            } else {
                Utils.showToast(this, "Permission is been granted");
            }
        }
    }

    public void openFloatingWindow(View view) {
        Utils.showToast(this, "open floating window");
        Intent intent = new Intent(this, FloatingWindowSvr.class);
        this.startService(intent);
    }

    public void exit(View view) {
        // exit this demo
        this.finish();
    }

    public void closeFloatingWindow(View view) {
        Utils.showToast(this, "close floating window");
        Intent intent = new Intent(this, FloatingWindowSvr.class);
        this.stopService(intent);
    }
}

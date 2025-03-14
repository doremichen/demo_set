/**
 * Demo shutdown item
 */
package com.adam.app.demoset.shutdown;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demo_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onExecute(View v) {
        Utils.showToast(this, "Execute is clicked!!!");
        Utils.info(this, "onExecute");
        try {
            Utils.info(this, "start to turn off!!!");
            Process proc = Runtime.getRuntime()
                    .exec(new String[]{ "reboot","-p" });
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
//        intent.putExtra("android.intent.extra.KEY_CONFIRM", true);
////        intent.putExtra(Intent.EXTRA_REASON,
////                PowerManager.SHUTDOWN_BATTERY_THERMAL_STATE);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

}
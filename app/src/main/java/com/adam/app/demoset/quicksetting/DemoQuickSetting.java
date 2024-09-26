package com.adam.app.demoset.quicksetting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoQuickSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_quick_setting);

        Utils.showAlertDialog(this, "Welcome to quick setting demo.", null);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void exit(View view) {
        finish();
    }
}

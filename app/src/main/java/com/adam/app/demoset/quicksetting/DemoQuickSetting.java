/**
 * Copyright (C) 2020 Adam demo set project. All rights reserved.
 * <p>
 * Description: This is the quick setting demo.
 * </p>
 *
 * Author: Adam Chen
 * Date: 2018/11/21
 */
package com.adam.app.demoset.quicksetting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.utils.UIUtils;

public class DemoQuickSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_quick_setting);

        UIUtils.applySystemBarInsets(findViewById(R.id.root_layout), findViewById(R.id.welcome_info));

        Utils.showAlertDialog(this,  getResources().getString(R.string.welcome_to_demo_quick_setting), null);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void exit(View view) {
        finish();
    }
}

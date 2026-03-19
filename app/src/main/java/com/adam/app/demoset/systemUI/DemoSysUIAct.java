/**
 * Copyright (C) 2019 Adam Demo set project. All rights reserved.
 * <p>
 * Description: This is demo system UI activity
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2018/10/16
 */
package com.adam.app.demoset.systemUI;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoSysUIAct extends AppCompatActivity {

    private Button mBtnDim;
    private Button mBtnHide;
    private TextView mTextInfo;
    private boolean mIsLowProfileMode;
    private boolean mIsProfileHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_sys_ui);

        // set fit system window as false
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

//  deprecate       // Show status bar and navigation
//        View decoreView = this.getWindow().getDecorView();
//        decoreView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mBtnDim = this.findViewById(R.id.ActDimSysUI);
        mBtnHide = this.findViewById(R.id.ActHideSysUI);
        mTextInfo = this.findViewById(R.id.info);

        updateStatusInfo("目前狀態：標準模式", androidx.appcompat.R.attr.colorPrimary);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onDimSysUI(View v) {
        Utils.info(this, "onDimSysUI enter");
        // get inset controller
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), v);

        if (controller == null) {
            Utils.info(this, "controller is null");
            return;
        }

        controller.setAppearanceLightStatusBars(!mIsLowProfileMode);
        controller.setAppearanceLightNavigationBars(!mIsLowProfileMode);

// deprecate       View decorView = v.getRootView();
//        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//
//        if (!mCanDim) {
//            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        }
//
        String msg = (mIsLowProfileMode) ? "標準模式" : "低亮模式" ;
        int color = (mIsLowProfileMode) ? androidx.appcompat.R.attr.colorPrimary :
                com.google.android.material.R.attr.colorTertiary;

        updateStatusInfo(msg, color);

        mBtnDim.setText(mIsLowProfileMode ? R.string.demo_system_ui_hide_low_light_btn : R.string.action_show_system_ui);

//        decorView.setSystemUiVisibility(uiOptions);
        // update
        mIsLowProfileMode = !mIsLowProfileMode;


    }

    public void onHideSysUI(View v) {
        Utils.info(this, "onHideSysUI enter");

        // get inset controller
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), v);

        if (controller == null) {
            Utils.info(this, "controller is null");
            return;
        }

        if (mIsProfileHide) {
            // show
            controller.show(WindowInsetsCompat.Type.systemBars());  // system bar
            controller.show(WindowInsetsCompat.Type.navigationBars()); // navigation bar
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

            if (getSupportActionBar()!= null) {
                getSupportActionBar().show();
            }

        } else {
            // hide
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.hide(WindowInsetsCompat.Type.navigationBars());

            if (getSupportActionBar()!= null) {
                getSupportActionBar().hide();
            }

        }

//  deprecate      View decorView = v.getRootView();
//        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//
//        if (!mCanHide) {
//            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            this.getSupportActionBar().hide();
//        } else {
//            this.getSupportActionBar().show();
//        }

        String msg = (mIsProfileHide) ? "標準模式" : "目前狀態：沉浸模式 (全螢幕)" ;
        int color = (mIsProfileHide) ? androidx.appcompat.R.attr.colorPrimary :
                com.google.android.material.R.attr.colorError;

        updateStatusInfo(msg, color);

        mBtnHide.setText(mIsProfileHide ? R.string.demo_system_ui_hide_invisible : R.string.action_show_system_ui);

        // update
        mIsProfileHide = !mIsProfileHide;
//        decorView.setSystemUiVisibility(uiOptions);

    }

    public void onExit(View v) {
        Utils.info(this, "onExit enter");
        this.finish();
    }

    private int getThemeColor(int attrResId) {
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(attrResId, typedValue, true)) {
            return typedValue.data;
        }
        return Color.GRAY; // default color
    }

    /**
     * Update status info
     *
     * @param message    message
     * @param colorAttr  color attribute
     */
    private void updateStatusInfo(String message, int colorAttr) {
        if (mTextInfo != null) {
            mTextInfo.setText(message);
            mTextInfo.setTextColor(getThemeColor(colorAttr));
        }
    }
}

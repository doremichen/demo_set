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

package com.adam.app.demoset.systemUI;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoSysUiBinding;
import com.adam.app.demoset.utils.UIUtils;

public class DemoSysUIAct extends AppCompatActivity {

    // view binding
    private ActivityDemoSysUiBinding mBbinding;


    private boolean mIsLowProfileMode;
    private boolean mIsProfileHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        mBbinding = ActivityDemoSysUiBinding.inflate(getLayoutInflater());
        setContentView(mBbinding.getRoot());

        UIUtils.applySystemBarInsets(mBbinding.getRoot(), mBbinding.appBarWrapper);


// Backup
//        // set fit system window as false
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//
//        // monitor window inset
//        ViewCompat.setOnApplyWindowInsetsListener(mBbinding.mainLayout, new OnApplyWindowInsetsListener() {
//
//            @Override
//            public @NonNull WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
//                Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//
//                // Make the top CardView avoid the Status Bar + action Bar.
//                int actionBarHeight = 0;
//                if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
//                    // can not used here because view is not ready at this moment
//                    // actionBarHeight = getSupportActionBar().getHeight();
//                    actionBarHeight = Utils.getActionBarHeight(DemoSysUIAct.this.getApplicationContext());
//                    Utils.info(DemoSysUIAct.this, "actionBarHeight: " + actionBarHeight);
//                }
//
//                // change card view margin
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBbinding.cardDescription.getLayoutParams();
//                params.topMargin = systemBar.top + actionBarHeight + 16;
//                mBbinding.cardDescription.setLayoutParams(params);
//
//                // Keep the bottom button bar away from the navigation bar
//                // This way, even if the navigation bar is displayed, the buttons won't be obscured.
//                mBbinding.layoutBtn.setPadding(
//                        mBbinding.layoutBtn.getPaddingLeft(),
//                        mBbinding.layoutBtn.getPaddingTop(),
//                        mBbinding.layoutBtn.getPaddingRight(),
//                        systemBar.bottom + 24
//                );
//                return insets;
//            }
//        });



//  deprecate       // Show status bar and navigation
//        View decoreView = this.getWindow().getDecorView();
//        decoreView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        updateStatusInfo(getString(R.string.demo_system_ui_noraml_state), androidx.appcompat.R.attr.colorPrimary);

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
        String msg = (mIsLowProfileMode) ? getString(R.string.demo_system_ui_noraml_state) : getString(R.string.demo_system_ui_low_light_mode);
        int color = (mIsLowProfileMode) ? androidx.appcompat.R.attr.colorPrimary :
                com.google.android.material.R.attr.colorTertiary;

        updateStatusInfo(msg, color);

        mBbinding.ActDimSysUI.setText(mIsLowProfileMode ? R.string.demo_system_ui_hide_low_light_btn : R.string.action_show_system_ui);

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

            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }

        } else {
            // hide
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.hide(WindowInsetsCompat.Type.navigationBars());

            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

        }

        // request apply insets
        ViewCompat.requestApplyInsets(getWindow().getDecorView());

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

        String msg = (mIsProfileHide) ? getString(R.string.demo_system_ui_noraml_state) : getString(R.string.demo_system_ui_immerse_mode);
        int color = (mIsProfileHide) ? androidx.appcompat.R.attr.colorPrimary :
                com.google.android.material.R.attr.colorError;

        updateStatusInfo(msg, color);

        mBbinding.ActHideSysUI.setText(mIsProfileHide ? R.string.demo_system_ui_hide_invisible : R.string.action_show_system_ui);

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
     * @param message   message
     * @param colorAttr color attribute
     */
    private void updateStatusInfo(String message, int colorAttr) {
        if (mBbinding.info != null) {
            mBbinding.info.setText(message);
            mBbinding.info.setTextColor(getThemeColor(colorAttr));
        }
    }
}

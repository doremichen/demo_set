package com.adam.app.demoset.systemUI;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoSysUIAct extends AppCompatActivity {

    Button mbtnDim;
    Button mBtnHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_sys_ui);

        // Show status bar and navigation
        View decoreView = this.getWindow().getDecorView();
        decoreView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mbtnDim = this.findViewById(R.id.ActDimSysUI);
        mBtnHide = this.findViewById(R.id.ActHideSysUI);
    }

    private boolean mCanDim;

    public void onDimSysUI(View v) {
        Utils.inFo(this, "onDimSysUI enter");
        View decoreView = v.getRootView();
        if (!mCanDim) {
            int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decoreView.setSystemUiVisibility(uiOptions);
            mbtnDim.setText(this.getResources().getString(R.string.action_show_system_ui));
            mCanDim = true;
        } else {
            decoreView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            mbtnDim.setText(this.getResources().getString(R.string.action_dim_system_ui));
            mCanDim = false;
        }

    }

    private boolean mCanHide;

    public void onHideSysUI(View v) {
        Utils.inFo(this, "onHideSysUI enter");
        View decoreView = v.getRootView();
        if (!mCanHide) {
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decoreView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = this.getSupportActionBar();
            actionBar.hide();
            mBtnHide.setText(this.getResources().getString(R.string.action_show_system_ui));
            mCanHide = true;
        } else {
            decoreView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            ActionBar actionBar = this.getSupportActionBar();
            actionBar.show();
            mBtnHide.setText(this.getResources().getString(R.string.action_hide_system_ui));
            mCanHide = false;
        }
    }

    public void onExit(View v) {
        Utils.inFo(this, "onExit enter");
        this.finish();
    }
}

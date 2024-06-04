package com.adam.app.demoset.systemUI;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
        Utils.info(this, "onDimSysUI enter");
        View decorView = v.getRootView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if (!mCanDim) {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            mbtnDim.setText(R.string.action_show_system_ui);
        } else {
            mbtnDim.setText(R.string.action_dim_system_ui);
        }

        decorView.setSystemUiVisibility(uiOptions);
        mCanDim = !mCanDim;

    }

    private boolean mCanHide;

    public void onHideSysUI(View v) {
        Utils.info(this, "onHideSysUI enter");
        View decorView = v.getRootView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if (!mCanHide) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            this.getSupportActionBar().hide();
            mBtnHide.setText(R.string.action_show_system_ui);
        } else {
            this.getSupportActionBar().show();
            mBtnHide.setText(R.string.action_hide_system_ui);
        }

        decorView.setSystemUiVisibility(uiOptions);
        mCanHide = !mCanHide;
    }

    public void onExit(View v) {
        Utils.info(this, "onExit enter");
        this.finish();
    }
}

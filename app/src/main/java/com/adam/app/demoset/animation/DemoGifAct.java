/**
 * Copyright (C) 2017 Adam Chen Demo set project. All rights reserved.
 *
 * <p>
 * Description:
 * This example is that demo how to play gif image.
 * The implement flow is as the following:
 * 1. Implement customize view: In this example the customize view is
 * GifView
 * 2. Add declare-styleable in values/attrs.xml. Reference:
 * https://developer.android.com/training/custom-views/create-view#customattr
 * 3. Make sure the attribute android:hardwareAccelerated="false" according to
 * show gif activity tag in AndroidManifest.xml
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2017/05/20
 */
package com.adam.app.demoset.animation;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

public class DemoGifAct extends AppCompatActivity implements GifView.GifStateListener {

    private GifView mGifView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_gif);

        UIUtils.applySystemBarInsets(findViewById(R.id.root_layout), findViewById(R.id.app_bar_wrapper));

        this.mGifView = (GifView) this.findViewById(R.id.gif_view);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onStop() {
        super.onStop();
        if (this.mGifView != null) {
            this.mGifView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        this.mGifView = null;
        super.onDestroy();
    }

    /**
     * Exit when press exit button
     *
     * @param view
     */
    public void onExit(View view) {
        // exit
        this.finish();
    }

    /**
     * Start to play gif image
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onStartToPlay(View view) {
        Utils.info(this, "[onStartToPlay] enter");
        if (this.mGifView == null) {
            Utils.showToast(this, "No gif image to play!!!");
            return;
        }

        this.mGifView.play();
    }

    /**
     * Stop to play gif image
     *
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onStopToPlay(View view) {
        Utils.info(this, "[onStopToPlay] enter");
        if (this.mGifView == null) {
            Utils.showToast(this, "No gif image to stop!!!");
            return;
        }

        this.mGifView.stop();
    }

    /**
     * The following methods are from GifView component
     */

    @Override
    public void onPlayGif() {
        Utils.showToast(this, getString(R.string.demo_animation_start_msg));
    }

    @Override
    public void onStopGif() {
        Utils.showToast(this, getString(R.string.demo_animation_stop_msg));
    }

    @Override
    public void onError(String msg) {
        Utils.showToast(this, getString(R.string.Demo_animation_error_msg, msg));
    }
}
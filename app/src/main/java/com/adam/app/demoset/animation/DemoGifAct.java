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
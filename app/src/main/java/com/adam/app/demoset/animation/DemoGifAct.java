/**
 * This example is that demo how to play gif image.
 * The implement flow is as the following:
 * 1. Implement customize view: In this example the customize view is
 *    GifView
 * 2. Add declare-styleable in values/attrs.xml. Reference:
 *    https://developer.android.com/training/custom-views/create-view#customattr
 * 3. Make sure the attribute android:hardwareAccelerated="false" according to
 *    show gif activity tag in AndroidManifest.xml
 */
package com.adam.app.demoset.animation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoGifAct extends AppCompatActivity implements GifView.GifStateListener {

    private GifView mGifView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_gif);

        this.mGifView = (GifView)this.findViewById(R.id.gif_view);
        // set resource
        this.mGifView.setGifData(R.drawable.animated_gif, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Exit when press exit button
     * @param view
     */
    public void onExit(View view) {
        // exit
        this.finish();
    }

    /**
     * Start to play gif image
     * @param view
     */
    public void onStartToPlay(View view) {
        Utils.info(this, "[onStartToPlay] enter");
        this.mGifView.play();
    }

    /**
     * Stop to play gif image
     * @param view
     */
    public void onStopToPlay(View view) {
        Utils.info(this, "[onStopToPlay] enter");
        this.mGifView.stop();
    }

    /**
     * The following methods are from GifView component
     */

    @Override
    public void onPlayGif() {
        Utils.showToast(this, "start to play gif!!!");
    }

    @Override
    public void onStopGif() {
        Utils.showToast(this, "stop to play gif!!!");
    }

    @Override
    public void onError(String msg) {
        Utils.showToast(this, msg);
    }
}
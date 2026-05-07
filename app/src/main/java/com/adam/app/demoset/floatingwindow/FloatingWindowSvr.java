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

package com.adam.app.demoset.floatingwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.floatingwindow.widget.CircleView;

/**
 * Handle the circle view start/stop
 */
public class FloatingWindowSvr extends Service {

    // window manager
    private WindowManager mWM;
    // window layout parameter
    private WindowManager.LayoutParams mLayoutParams;
    // circle view
    private CircleView mCircleView;
    // frame layout
    private FrameLayout mLayout;


    // touch event data
    private float mInitialTouchX;
    private float mInitialTouchY;
    private int mInitialWindowX;
    private int mInitialWindowY;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.info(this, "onCreate");
        // get window service
        this.mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        buildFloatingView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy");
        // removew circle view to dismiss the floating view
        if (Utils.areAllNotNull(this.mWM, this.mLayout)) {
            this.mWM.removeView(this.mLayout);
        }
    }

    private void buildFloatingView() {
        Utils.info(this, "buildFloatingView");
        // precondiftion
        if (!Utils.areAllNotNull(this.mWM)) {
            Utils.showToast(this, "No window manager!!!!");
            return;
        }

        // Create WindowManager.LayoutParams
        this.mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888
        );
        this.mLayoutParams.gravity = Gravity.CENTER;

        // Inflate the layout
        this.mLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.float_layout, null);

        if (!Utils.areAllNotNull(this.mLayout)) {
            Utils.showToast(this, "null layout!!!!");
            return;
        }

        // Add the view to the window manager
        mWM.addView(this.mLayout, this.mLayoutParams);

        // Get a reference to the circular view
        View circleView = this.mLayout.findViewById(R.id.float_id);

        // Set the touch event listener
        circleView.setOnTouchListener(this::handleTouch);
    }

    private boolean handleTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouchX = event.getRawX();
                mInitialTouchY = event.getRawY();
                mInitialWindowX = mLayoutParams.x;
                mInitialWindowY = mLayoutParams.y;
                return false;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getRawX() - mInitialTouchX;
                float deltaY = event.getRawY() - mInitialTouchY;
                
                mLayoutParams.x = mInitialWindowX + (int) deltaX;
                mLayoutParams.y = mInitialWindowY + (int) deltaY;
                
                if (Utils.areAllNotNull(mWM, mLayout)) {
                    mWM.updateViewLayout(mLayout, mLayoutParams);
                }
                return false;
        }
        return false;
    }

}

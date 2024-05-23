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
import com.adam.app.demoset.Utils;
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


    private static final long LONG_PRESS_TIME = 500L;

    // touch event data
    private static class Current {
        static float sX;
        static float sY;
        static long time;
    }

    private static class Previous {
        static float sX;
        static float sY;
        static long time;
    }


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
        // get window service and layout params
        this.mLayoutParams = new WindowManager.LayoutParams();
        this.mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        buildFloatingView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy");
        // removew circle view to dismiss the floating view
        if (this.mLayout != null
            || this.mLayoutParams != null) {
            this.mWM.removeView(this.mLayout);
        }
    }

    private void buildFloatingView() {
        Utils.info(this, "buildFloatingView");
        // config layout params
        this.mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        this.mLayoutParams.format = PixelFormat.RGBA_8888; // the effect is transparent
        this.mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        this.mLayoutParams.gravity = Gravity.CENTER;
        this.mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        this.mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // start inflat
        LayoutInflater inflater = LayoutInflater.from(this.getApplicationContext());
        this.mLayout = (FrameLayout)inflater.inflate(R.layout.float_layout, null);

        // measure layout
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        this.mLayout.measure(width, height);

        // add view by window service
        this.mWM.addView(this.mLayout, this.mLayoutParams);
        // get circle view handler
        this.mCircleView = this.mLayout.findViewById(R.id.float_id);

        // monitor touch event of the circle view
        this.mCircleView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.info(this, "onTouch");
//                StringBuilder stb = new StringBuilder("Refresh UI: \n");
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Current.sX = event.getRawX();
                        Current.sY = event.getRawY();
                        Previous.sX = Current.sX;
                        Previous.sY = Current.sY;
                        Previous.time = event.getDownTime();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - Current.sX;
                        float deltaY = event.getRawY() - Current.sY;
                        // get event position and time
                        Current.sX = event.getRawX();
                        Current.sY = event.getRawY();
                        Current.time = event.getEventTime();

                        if (isLongPress()) {
                            // update layout position
                            mLayoutParams.x += (int)deltaX;
                            mLayoutParams.y += (int)deltaY;
                            // refresh UI
                            Utils.info(this, "refresh UI...");
                            mWM.updateViewLayout(mLayout, mLayoutParams);
                        }
//                        stb.append("event x: ").append(event.getRawX()).append(" ").append(Current.sY);
//                        stb.append("\n");
//                        stb.append("event y: ").append(event.getRawY()).append(" ").append(Current.sY);
//                        stb.append("\n");
//                        stb.append("deltaX: ").append(deltaX).append(" ").append(Current.sY);
//                        stb.append("\n");
//                        stb.append("deltaY: ").append(deltaY).append(" ").append(Current.sY);
//                        stb.append("\n");
//                        stb.append("mLayoutParams.x: ").append(mLayoutParams.x).append(" ").append(Current.sY);
//                        stb.append("\n");
//                        stb.append("mLayoutParams.y: ").append(mLayoutParams.y).append(" ").append(Previous.sY);
//                        stb.append("\n");
                        break;
                }
//                Utils.inFo(this, stb.toString());
                return false;
            }
        });


    }

    private boolean isLongPress() {
        float offsetX = Math.abs(Previous.sX - Current.sX);
        float offsetY = Math.abs(Previous.sY - Current.sY);
        long interValTime = Current.time - Previous.time;
//        StringBuilder stb = new StringBuilder("isLongPress: \n");
//        stb.append("offsetX: ").append(offsetX).append("\n");
//        stb.append("offsetY: ").append(offsetY).append("\n");
//        stb.append("interValTime: ").append(interValTime).append("\n");
//        Utils.inFo(this, stb.toString());
        if (offsetX >= 10.0f && offsetY >= 10.0f && interValTime >= LONG_PRESS_TIME) {
            return true;
        }

        return false;
    }
}

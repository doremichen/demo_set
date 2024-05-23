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

        // Use Builder pattern to create WindowManager.LayoutParams
        this.mLayoutParams = new LayoutParamsBuilder()
                .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                .setFormat(PixelFormat.RGBA_8888)
                .setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                .setGravity(Gravity.CENTER)
                .setWidth(WindowManager.LayoutParams.WRAP_CONTENT)
                .setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
                .build();

        // Use Factory pattern to create FrameLayout
        this.mLayout = LayoutFactory.createFloatingLayout(getApplicationContext());

        // Measure the size of the floating view
        this.mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // Add the view to the window manager
        mWM.addView(this.mLayout, this.mLayoutParams);

        // Get a reference to the circular view
        View circleView = this.mLayout.findViewById(R.id.float_id);

        // Set the touch event listener using Strategy pattern
        TouchStrategy touchStrategy = new DefaultTouchStrategy();
        circleView.setOnTouchListener(touchStrategy::onTouch);
    }

    private static class LayoutParamsBuilder {
        private final WindowManager.LayoutParams mLayoutParams;

        public LayoutParamsBuilder() {
            mLayoutParams = new WindowManager.LayoutParams();
        }

        public LayoutParamsBuilder setType(int type) {
            mLayoutParams.type = type;
            return this;
        }

        public LayoutParamsBuilder setFormat(int format) {
            mLayoutParams.format = format;
            return this;
        }

        public LayoutParamsBuilder setFlags(int flags) {
            mLayoutParams.flags = flags;
            return this;
        }

        public LayoutParamsBuilder setGravity(int gravity) {
            mLayoutParams.gravity = gravity;
            return this;
        }

        public LayoutParamsBuilder setWidth(int width) {
            mLayoutParams.width = width;
            return this;
        }

        public LayoutParamsBuilder setHeight(int height) {
            mLayoutParams.height = height;
            return this;
        }

        public WindowManager.LayoutParams build() {
            return mLayoutParams;
        }
    }

    private static class LayoutFactory {
        public static FrameLayout createFloatingLayout(Context context) {
            LayoutInflater inflater = LayoutInflater.from(context);
            return (FrameLayout) inflater.inflate(R.layout.float_layout, null);
        }
    }

    private interface TouchStrategy {
        boolean onTouch(View v, MotionEvent event);
    }

    private class DefaultTouchStrategy implements TouchStrategy {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Utils.info(this, "onTouch");
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Utils.info(this, "Down");
                Previous.sX = Current.sX = event.getRawX();
                Previous.sY = Current.sY = event.getRawY();
                Previous.time = event.getDownTime();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Utils.info(this, "Long Move");
                float deltaX = event.getRawX() - Current.sX;
                float deltaY = event.getRawY() - Current.sY;
                Current.sX = event.getRawX();
                Current.sY = event.getRawY();
                Current.time = event.getEventTime();

                if (isLongPress()) {
                    FloatingWindowSvr.this.mLayoutParams.x += (int) deltaX;
                    FloatingWindowSvr.this.mLayoutParams.y += (int) deltaY;
                    Utils.info(this, "updateViewLayout");
                    FloatingWindowSvr.this.mWM.updateViewLayout(FloatingWindowSvr.this.mLayout,
                            FloatingWindowSvr.this.mLayoutParams);
                }
            }
            return false;
        }
    }

    private boolean isLongPress() {
        Utils.info(this, "isLongPress");
        float offset = Math.max(Math.abs(Previous.sX - Current.sX), Math.abs(Previous.sY - Current.sY));
        long duration = Current.time - Previous.time;
        Utils.info(this, "offset: " + offset);
        Utils.info(this, "duration: " + duration);
        return offset >= 10.0f && duration >= LONG_PRESS_TIME;
    }
}

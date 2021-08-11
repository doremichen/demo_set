package com.adam.app.demoset.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

class GifView extends android.support.v7.widget.AppCompatImageView {

    /**
     * Used to call back of gif play
     */
    interface GifStateListener{
        void onPlayGif();
        void onStopGif();
        void onError(String msg);
    }

    private GifStateListener mListener;
    private Movie mMovie;
    private long mMovieStart;
    private int mDuration;
    private boolean mIsStart;

    private static final int DEFAULT_DURATION = 1000;   // set movie time


    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifView);
        int srcId = a.getResourceId(R.styleable.GifView_gifSrc, -1);
        // set image
        setImage(srcId);
        a.recycle();
    }



    public GifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Utils.info(this, "[onMeasure] enter");
        if (this.mMovie != null) {
            // fill big image file size
            int movieWidth = this.mMovie.width();
            int movieHeight = this.mMovie.height();
            // set dimension
            setMeasuredDimension(movieWidth, movieHeight);

        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Utils.info(this, "[onDraw] enter");
        if (this.mMovie == null) {
            showErrorInfo("No gif image to draw!!!");
            super.onDraw(canvas);
            return;
        }


        // movie draw
        drawGif(canvas);
        // check start flag
        if (this.mIsStart == true) {
            invalidate();
        }
    }

    public void setGifData(int resId, GifStateListener listener) {
        Utils.info(this, "[setGifData] enter");
        this.mListener = listener;
        // init movie
        this.mMovie = Movie.decodeStream(getResources().openRawResource(resId));
        // try to show bitmap if no gif image
        if (this.mMovie == null) {
            Utils.info(this, "No gif image to show!!!");
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
            if (bmp != null) {
                // set image
                setImageBitmap(bmp);
                return;
            }
        }

        this.mDuration = (this.mMovie.duration() == 0)? DEFAULT_DURATION: this.mMovie.duration();
        // set show screen size
        requestLayout();
    }

    /**
     * Start to play gif image
     */
    public void play() {
        Utils.info(this, "[play] enter");
        if (this.mIsStart == true) {
            showErrorInfo("The gif is playing!!!");
            return;
        }

        // set flag
        this.mIsStart = true;

        // start to draw
        invalidate();
        // call back
        if (this.mListener != null) {
            this.mListener.onPlayGif();
        }
    }

    /**
     * Stop to play gif image
     */
    public void stop() {
        Utils.info(this, "[stop] enter");

        // set flag
        this.mIsStart = false;

        // call back
        if (this.mListener != null) {
            this.mListener.onStopGif();
        }
    }

    private void setImage(int resId) {
        Utils.info(this, "[setImage] enter");
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resId);
        if (bmp != null) {
            // set image
            setImageBitmap(bmp);
        }
    }

    private void showErrorInfo(String msg) {
        if (this.mListener != null) {
            this.mListener.onError(msg);
        }
    }


    /**
     * Call by onDraw to show gif image
     * @param canvas
     */
    private void drawGif(Canvas canvas) {
        Utils.info(this, "[drawGif] enter");
        // set time
        this.mMovie.setTime(getCurrentFrameTime());
        // draw
        this.mMovie.draw(canvas, 0.0f, 0.0f);
    }


    /**
     * Get current gif frame time
     * @return
     */
    private int getCurrentFrameTime() {
        Utils.info(this, "[getCurrentFrameTime] enter");
        long now = SystemClock.uptimeMillis();
        this.mMovieStart = (this.mMovieStart == 0)? now: this.mMovieStart;
        int currentTime = (int) ((now - this.mMovieStart) % this.mDuration);
        return currentTime;
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("= GifView Object: ==================").append("\n");
        stb.append("mListener: ").append(mListener).append("\n");
        stb.append("mMovie: ").append(mMovie).append("\n");
        stb.append("mMovieStart: ").append(mMovieStart).append("\n");
        stb.append("mDuration: ").append(mDuration).append("\n");
        stb.append("mIsStart: ").append(mIsStart).append("\n");
        stb.append("====================================").append("\n");
        return stb.toString();
    }
}

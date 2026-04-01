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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

public class GifView extends AppCompatImageView {

    private GifStateListener mListener;
    private AnimatedImageDrawable mAnimatedDrawable;
    private boolean mIsPlaying = false;

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Utils.info(this, "Warning: AnimatedImageDrawable requires API 28+");
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifView);
        int srcId = a.getResourceId(R.styleable.GifView_gifSrc, -1);
        // set image
        if (srcId != -1) {
            setGifResource(srcId, null);
        }
        // recycle
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setGifResource(int srcId, GifStateListener listener) {
        this.mListener = listener;

        try {
            // get img source
            ImageDecoder.Source source = ImageDecoder.createSource(getResources(), srcId);
            // create drawable
            Drawable drawable = ImageDecoder.decodeDrawable(source);

            setImageDrawable(drawable);

            if (drawable instanceof AnimatedImageDrawable) {
                mAnimatedDrawable = (AnimatedImageDrawable) drawable;
                // set repeat
                mAnimatedDrawable.setRepeatCount(AnimatedImageDrawable.REPEAT_INFINITE);
                Utils.info(this, "Success: AnimatedImageDrawable initialized.");
            } else {
                showErrorInfo("Resource is not an animated image.");
            }
        } catch (Exception e) {
            showErrorInfo("Error loading gif: " + e.getMessage());
            Utils.info(this, "Error: " + e.getMessage());
        }
    }

    /**
     * Start to play gif image
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void play() {
        Utils.info(this, "[play] enter");
        // precheck
        if (this.mAnimatedDrawable == null) {
            showErrorInfo("No gif image to play!!!");
            return;
        }

        if (this.mIsPlaying == true) {
            showErrorInfo("The gif is playing!!!");
            return;
        }

        // start
        this.mAnimatedDrawable.start();
        this.mIsPlaying = true;

        // callback
        if (this.mListener != null) {
            this.mListener.onPlayGif();
        }
        Utils.info(this, "play xxx");
    }

    /**
     * Stop to play gif image
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void stop() {
        Utils.info(this, "[stop] enter");
        // precheck
        if (this.mAnimatedDrawable == null) {
            showErrorInfo("No gif image to stop!!!");
            return;
        }

        if (this.mIsPlaying == false) {
            showErrorInfo("The gif is not playing!!!");
            return;
        }

        // stop
        this.mAnimatedDrawable.stop();
        this.mIsPlaying = false;

        // callback
        if (this.mListener != null) {
            this.mListener.onStopGif();
        }

        Utils.info(this, "stop xxx");
    }

    private void showErrorInfo(String msg) {
        if (this.mListener != null) {
            this.mListener.onError(msg);
        }
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append("= GifView Object: ==================").append("\n");
        stb.append("mListener: ").append(mListener).append("\n");
        stb.append("mAnimatedDrawable: ").append(mAnimatedDrawable).append("\n");
        stb.append("mIsPlaying: ").append(mIsPlaying).append("\n");
        stb.append("====================================").append("\n");
        return stb.toString();
    }

    /**
     * Used to call back of gif play
     */
    interface GifStateListener {
        void onPlayGif();

        void onStopGif();

        void onError(String msg);
    }
}

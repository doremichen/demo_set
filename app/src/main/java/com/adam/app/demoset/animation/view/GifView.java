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

package com.adam.app.demoset.animation.view;

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
import androidx.databinding.BindingAdapter;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

/**
 * A custom ImageView to display and control GIF animations using AnimatedImageDrawable.
 */
public class GifView extends AppCompatImageView {

    private GifStateListener mListener;
    private AnimatedImageDrawable mAnimatedDrawable;
    private boolean mIsPlaying = false;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
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

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifView);
            int srcId = a.getResourceId(R.styleable.GifView_gifSrc, -1);
            if (srcId != -1) {
                setGifResource(srcId);
            }
            a.recycle();
        }
    }

    /**
     * Sets the listener for GIF playback states.
     */
    public void setGifStateListener(GifStateListener listener) {
        this.mListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setGifResource(int srcId) {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getResources(), srcId);
            Drawable drawable = ImageDecoder.decodeDrawable(source);

            setImageDrawable(drawable);

            if (drawable instanceof AnimatedImageDrawable) {
                mAnimatedDrawable = (AnimatedImageDrawable) drawable;
                mAnimatedDrawable.setRepeatCount(AnimatedImageDrawable.REPEAT_INFINITE);
                Utils.info(this, "AnimatedImageDrawable initialized successfully.");
            } else {
                showErrorInfo("Provided resource is not an animated image.");
            }
        } catch (Exception e) {
            showErrorInfo("Error loading GIF: " + e.getMessage());
        }
    }

    /**
     * Starts playing the GIF.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void play() {
        if (mAnimatedDrawable == null) {
            showErrorInfo("No GIF loaded to play.");
            return;
        }

        if (mIsPlaying) {
            return;
        }

        mAnimatedDrawable.start();
        mIsPlaying = true;

        if (mListener != null) {
            mListener.onPlayGif();
        }
    }

    /**
     * Stops playing the GIF.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void stop() {
        if (mAnimatedDrawable == null) {
            return;
        }

        if (!mIsPlaying) {
            return;
        }

        mAnimatedDrawable.stop();
        mIsPlaying = false;

        if (mListener != null) {
            mListener.onStopGif();
        }
    }

    private void showErrorInfo(String msg) {
        Utils.error(this, msg);
        if (mListener != null) {
            mListener.onError(msg);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "GifView{" +
                "mIsPlaying=" + mIsPlaying +
                ", hasDrawable=" + (mAnimatedDrawable != null) +
                '}';
    }

    /**
     * Callback interface for GIF state changes.
     */
    public interface GifStateListener {
        void onPlayGif();
        void onStopGif();
        void onError(String msg);
    }
}

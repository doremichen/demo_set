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

package com.adam.app.demoset.utils;

import android.content.res.ColorStateList;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.adam.app.demoset.animation.view.GifView;
import com.google.android.material.button.MaterialButton;

public class UIBindingAdapters {

    @BindingAdapter(value = {"android:onClick", "hideKeyboardOnClick"}, requireAll = false)
    public static void setHideKeyboardOnClick(View view, View.OnClickListener listener, boolean hide) {
        view.setOnClickListener(v -> {
            if (hide) {
                Utils.hideSoftKeyBoardFrom(view.getContext(), view);
            }

            if (listener != null) {
                listener.onClick(v);
            }
        });
    }

    @BindingAdapter("dynamicTextSize")
    public static void setDynamicTextSize(TextView view, float scale) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16 * scale);
    }

    /**
     * Specific BindingAdapter for MaterialButton background tint to solve DataBinding setter issue.
     */
    @BindingAdapter("backgroundTint")
    public static void setMaterialButtonBackgroundTint(MaterialButton button, int color) {
        button.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    /**
     * DataBinding Adapter to control playback state.
     */
    @BindingAdapter("isPlaying")
    public static void setIsPlaying(GifView view, boolean isPlaying) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (isPlaying) {
                view.play();
            } else {
                view.stop();
            }
        }
    }
}

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

import android.app.Activity;
import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public final class UIUtils {

    private UIUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * Apply system bar insets to the given root layout and app bar.
     * @param rootLayout The root layout of the activity.
     * @param appBar The app bar of the activity.
     */
    public static void applySystemBarInsets(View rootLayout, View appBar) {
        // input validation check
        if (rootLayout == null) {
            return;
        }

        Activity activity = (Activity)rootLayout.getContext();
        if (activity == null) {
            return;
        }

        Window window = activity.getWindow();

        // set fit system windows as false
        WindowCompat.setDecorFitsSystemWindows(window, false);

        // set inset listener
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            // get system bar
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // set padding
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            // toobar
            if (appBar != null) {
                appBar.setPadding(0, bars.top, 0, 0);
            }

            return insets; //WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * Hide system bar
     * @param window window
     */
    public static void hideSystemBar(Window window) {
        if (window == null) {
            return;
        }

        // assure fit system windows as false
        WindowCompat.setDecorFitsSystemWindows(window, false);

        // get inset window controller
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller == null) {
            return;
        }

        // hide system bar
        controller.hide(WindowInsetsCompat.Type.systemBars());
        // set immerse behavior when swipe
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

}

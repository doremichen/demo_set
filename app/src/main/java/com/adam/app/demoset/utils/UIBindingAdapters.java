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
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.adam.app.demoset.animation.view.GifView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UIBindingAdapters {

    /** Tag for logging */
    private static final String TAG = "UIBindingAdapters";

    /** Player X symbol */
    private static final char PLAYER_X = 'X';

    /** Player O symbol */
    private static final char PLAYER_O = 'O';

    /** Empty cell symbol */
    private static final char EMPTY_SYMBOL = '\0';

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

    /**
     * Set text color from theme attribute.
     */
    @BindingAdapter("textColorAttr")
    public static void setTextColorAttr(TextView view, int attrResId) {
        if (attrResId == 0) return;
        TypedValue typedValue = new TypedValue();
        if (view.getContext().getTheme().resolveAttribute(attrResId, typedValue, true)) {
            view.setTextColor(typedValue.data);
        }
    }

    /**
     * Binds the board cell state to a button.
     *
     * @param button       The button to bind.
     * @param board        The game board state.
     * @param index        The cell index.
     * @param winningCells The list of winning cell indices.
     */
    @BindingAdapter(value = {"board", "cellIndex", "winningCells"}, requireAll = true)
    public static void bindBoardCell(Button button, char[] board, int index, List<Integer> winningCells) {
        Utils.log(TAG, "bindBoardCell: " + index);

        // Input validation
        if (board == null || index < 0 || index >= board.length) {
            // Clear button info on invalid state
            button.setText("");
            button.setBackgroundColor(Color.LTGRAY);
            return;
        }

        // Display the mark
        char symbol = board[index];
        button.setText((symbol == EMPTY_SYMBOL) ? "" : String.valueOf(symbol));

        // Determine colors based on cell state
        int baseColor = Color.LTGRAY;
        int textColor = Color.BLACK;

        if (symbol == PLAYER_X) {
            textColor = Color.BLUE;
        } else if (symbol == PLAYER_O) {
            textColor = Color.RED;
        }

        // Highlight if this cell is part of a winning combination
        if (winningCells != null && winningCells.contains(index)) {
            baseColor = Color.YELLOW;
        }

        button.setBackgroundColor(baseColor);
        button.setTextColor(textColor);
    }
}

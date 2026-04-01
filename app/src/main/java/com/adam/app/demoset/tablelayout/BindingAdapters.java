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

package com.adam.app.demoset.tablelayout;

import android.graphics.Color;
import android.widget.Button;

import androidx.databinding.BindingAdapter;

import com.adam.app.demoset.utils.Utils;

import java.util.List;

public class BindingAdapters {
    // TAG
    private static final String TAG = "BindingAdapters";


    @BindingAdapter(value = {"board", "cellIndex", "winningCells"}, requireAll = true)
    public static void bindBoardCell(Button button, char[] board, int index, List<Integer> winningCells) {
        Utils.log(TAG, "bindBoardCell: " + index);
        // input check
        if (board == null ||index < 0 || index >= board.length) {
            // clear button info
            button.setText("");
            button.setBackgroundColor(Color.LTGRAY);
            return;
        }

        // Display
        char c = board[index];
        button.setText((c == '\0')? "": String.valueOf(c));

        // color difference
        int baseColor = Color.LTGRAY;
        int textColor = Color.BLACK;

        if (c == 'X') {
            textColor = Color.BLUE;
        } else if (c == 'O') {
            textColor = Color.RED;
        }

        if (winningCells != null && winningCells.contains(index)) {
            baseColor = Color.YELLOW;
        }

        button.setBackgroundColor(baseColor);
        button.setTextColor(textColor);

    }

}
